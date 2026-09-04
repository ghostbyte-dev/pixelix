package com.daniebeler.pfpixelix.ui.composables.post_editor

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.model.request.NewPostRequest
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.AccountState
import com.daniebeler.pfpixelix.utils.BlurHashEncoder
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.io
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Clock
import kotlin.time.Instant

enum class EditorMode { CREATE, EDIT }

class PostEditorViewModel @Inject constructor(
    private val postEditorService: PostEditorService,
    private val exploreService: ExploreService,
    private val postService: PostService,
    private val instanceService: InstanceService,
    private val fileService: FileService,
    private val platform: Platform,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager,
    private val accountService: AccountService,
    session: Session,
    userPreferences: UserPreferences
) : ViewModel() {
    data class EditorMediaItem(
        val imageUri: KmpUri,
        val mimeType: String?,
        var id: String?,
        var isLoading: Boolean,
        var isError: Boolean,
        var locationInitialValue: Location?,
        var metadata: MediaAttachmentMetadataRequest
    )

    val capabilities = session.capabilities

    var mode by mutableStateOf(EditorMode.CREATE)
    var editingPostId: String? = null

    var mediaItems = mutableStateListOf<EditorMediaItem>()
    var caption by mutableStateOf(TextFieldValue())
    var locationId: String by mutableStateOf("")
    var isSensitive: Boolean by mutableStateOf(false)
    var contentWarning: String by mutableStateOf("")
    var visibility: Visibility by mutableStateOf(Visibility.PUBLIC)
    var areCommentsDisabled: Boolean by mutableStateOf(false)
    var mediaUploadState by mutableStateOf(MediaUploadState())
    var postSubmissionState by mutableStateOf(PostSubmissionState())
    var instance: Instance? = null
    var mediaAdditionError by mutableStateOf(AddMediaError())
    var compressionLoading by mutableStateOf(false)
    var accountState by mutableStateOf(AccountState())

    var isOnGeneralPage by mutableStateOf(false)
    var categoriesState by mutableStateOf(CategoriesState())
    var licensesState by mutableStateOf(LicensesState())
    var defaultLicense by mutableStateOf<License?>(null)

    private var originalContent: String = ""
    private var originalSensitive: Boolean = false
    private var originalCommentsDisabled: Boolean = false
    private var originalContentWarning: String = ""
    private var originalLocationId: String = ""
    private var originalMediaIds = listOf<String>()
    private var originalMediaMetadata = mapOf<String, MediaAttachmentMetadataRequest>()

    val isEdited: Boolean by derivedStateOf {
        if (mode == EditorMode.CREATE) {
            true
        } else {
            val currentMediaIds = mediaItems.mapNotNull { it.id }

            val currentMediaChanged = mediaItems.any { image ->
                val originalMetadata = originalMediaMetadata[image.id] ?: ""
                image.metadata!= originalMetadata
            }

            caption.text != originalContent || isSensitive != originalSensitive || contentWarning != originalContentWarning || locationId != originalLocationId || currentMediaIds != originalMediaIds || areCommentsDisabled != originalCommentsDisabled || currentMediaChanged
        }
    }

    init {
        viewModelScope.launch {
            getInstance()
            getAccount()
            getCategories()
            getLicenses()
        }

        userPreferences.captionTemplateFlow.onEach { caption = TextFieldValue(it) }
            .launchIn(viewModelScope)

        userPreferences.defaultVisibilityFlow.onEach { visibility = it }.launchIn(viewModelScope)

        userPreferences.defaultLicenseFlow.onEach { defaultLicense = it }
            .launchIn(viewModelScope)
    }


    fun initForEdit(postId: String) {
        if (mode == EditorMode.EDIT && editingPostId == postId) return

        mode = EditorMode.EDIT
        editingPostId = postId
        isOnGeneralPage =
            true

        postSubmissionState = PostSubmissionState(isLoading = true)

        postService.getPostById(postId).onEach { result ->
            postSubmissionState = when (result) {
                is Resource.Success -> {
                    val post = result.data

                    caption = TextFieldValue(post.content)
                    isSensitive = post.sensitive
                    contentWarning = post.spoilerText
                    visibility = post.visibility
                    categoriesState = categoriesState.copy(selectedCategory = post.category)

                    originalContent = post.content
                    originalSensitive = post.sensitive
                    originalContentWarning = post.spoilerText
                    originalLocationId = post.location?.id ?: ""
                    originalCommentsDisabled = post.commentsDisabled
                    originalMediaIds = post.mediaAttachments.map { it.id }

                    post.location?.let {
                        locationId = it.id
                    }
                    val mappedImages = post.mediaAttachments.map { media ->
                        EditorMediaItem(
                            imageUri = media.previewUrl?.toKmpUri() ?: media.url.toKmpUri(),
                            mimeType = media.type ?: "image/jpeg",
                            id = media.id,
                            isLoading = false,
                            isError = false,
                            locationInitialValue = media.location,
                            metadata = MediaAttachmentMetadataRequest(
                                id = media.id,
                                description = media.description ?: "",
                                blurhash = media.blurHash,
                                locationId = media.location?.id,
                                license = media.license,
                                lens = FieldState(media.metadata?.lens),
                                make = FieldState(media.metadata?.make),
                                model = FieldState(media.metadata?.model),
                                flash = FieldState(media.metadata?.flash),
                                focalLength = FieldState(media.metadata?.focalLength),
                                focalLenIn35mmFilm = FieldState(media.metadata?.focalLenIn35mmFilm),
                                fNumber = FieldState(media.metadata?.fNumber),
                                exposureTime = FieldState(media.metadata?.exposureTime),
                                photographicSensitivity = FieldState(media.metadata?.photographicSensitivity),
                                software = FieldState(media.metadata?.software),
                                createDate = FieldState(media.metadata?.createDate?.let {
                                    Instant.parse(
                                        it
                                    )
                                }),
                                film = FieldState(media.metadata?.film),
                                chemistry = FieldState(media.metadata?.chemistry),
                                scanner = FieldState(media.metadata?.scanner),
                            )
                        )
                    }

                    mediaItems.clear()
                    mediaItems.addAll(mappedImages)

                    originalMediaMetadata =
                        mappedImages.associate { (it.id ?: "") to (it.metadata) }

                    mediaUploadState = MediaUploadState(
                        mediaAttachments = post.mediaAttachments, isLoading = false
                    )

                    PostSubmissionState()
                }

                is Resource.Error -> {
                    PostSubmissionState(error = result.message)
                }

                is Resource.Loading -> {
                    PostSubmissionState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private val navigationEffectChannel = Channel<PostEditorNavigationEffect>(Channel.BUFFERED)
    val navigationEffects = navigationEffectChannel.receiveAsFlow()

    fun submitPost() {
        if (mediaItems.find { it.isLoading } != null) return // Wait for uploads

        postSubmissionState = PostSubmissionState(isLoading = true)

        // Trigger metadata updates for images if they were changed
        mediaItems.forEachIndexed { index, _ -> updateMetadata(index) }

        if (mode == EditorMode.CREATE) {
            mediaUploadState = sortMediaUploadState(mediaUploadState)
            createNewPost(mediaUploadState)
        } else {
            updateExistingPost()
        }
    }

    private fun updateExistingPost() {
        val postId = editingPostId ?: return
        val mediaIds = mediaItems.mapNotNull { it.id }
        val locationIdNullable = locationId.ifBlank { null }

        val updateRequest = NewPostRequest(
            note = caption.text,
            mediaIds = mediaIds,
            sensitive = isSensitive,
            visibility = visibility,
            contentWarning = contentWarning,
            placeId = locationIdNullable,
            commentsDisabled = areCommentsDisabled,
            categoryId = categoriesState.selectedCategory?.id
        )

        postEditorService.updatePost(postId, updateRequest).onEach { result ->
            postSubmissionState = when (result) {
                is Resource.Success -> {
                    navigationEffectChannel.trySend(
                        PostEditorNavigationEffect.PostUpdated(postId)
                    )
                    PostSubmissionState()
                }

                is Resource.Error -> {
                    PostSubmissionState(error = result.message)
                }

                is Resource.Loading -> {
                    PostSubmissionState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateCaption(newCaption: TextFieldValue) {
        caption = newCaption
        hashtagMentionsSuggestionsManager.changeText(newCaption, viewModelScope)
    }


    private fun getLicenses() {
        exploreService.getLicenses().onEach { result ->
            licensesState = when (result) {
                is Resource.Success -> {
                    LicensesState(licenses = result.data)
                }

                is Resource.Error -> {
                    LicensesState(
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    LicensesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getCategories() {
        exploreService.getCategories().onEach { result ->
            categoriesState = when (result) {
                is Resource.Success -> {
                    CategoriesState(categories = result.data)
                }

                is Resource.Error -> {
                    CategoriesState(
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    CategoriesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getInstance() {
        instanceService.getInstance().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    instance = result.data
                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAccount() {
        accountService.getOwnAccount().onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
//                    if (result.data.locked) {
//                        audience = Visibility.PRIVATE
//                    }
                    AccountState(account = result.data)
                }

                is Resource.Error -> {
                    AccountState(error = result.message)
                }

                is Resource.Loading -> {
                    accountState.copy(isLoading = true, refreshing = false)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun updateImageMetadata(index: Int, newMetadata: MediaAttachmentMetadataRequest) {
        mediaItems = mediaItems.also {
            it[index] = it[index].copy(metadata = newMetadata)
        }
    }

    private fun bytesIntoHumanReadable(bytes: Long): String? {
        val kilobyte: Long = 1024
        val megabyte = kilobyte * 1024
        val gigabyte = megabyte * 1024
        val terabyte = gigabyte * 1024
        return if (bytes in 0..<kilobyte) {
            "$bytes B"
        } else if (bytes in kilobyte..<megabyte) {
            (bytes / kilobyte).toString() + " KB"
        } else if (bytes in megabyte..<gigabyte) {
            (bytes / megabyte).toString() + " MB"
        } else if (bytes in gigabyte..<terabyte) {
            (bytes / gigabyte).toString() + " GB"
        } else if (bytes >= terabyte) {
            (bytes / terabyte).toString() + " TB"
        } else {
            "$bytes Bytes"
        }
    }

    fun addImage(uri: KmpUri, metadata: MediaAttachmentMetadataRequest) {
        val file = PlatformFile(uri)
        if (!fileService.exists(file)) {
            return
        }
        val fileType = fileService.getMimeType(file)
        if (instance != null && !instance!!.configuration.mediaAttachmentConfig.supportedMimeTypes.contains(
                fileType
            )
        ) {
            mediaAdditionError = AddMediaError(
                AddMediaErrorType.ERROR,
                "Media type is not supported",
                "The media type $fileType is not supported by this server"
            )
            return
        }
        val size = file.size()

        if (fileType.take(5) == "image") {
            if (instance != null && size > instance!!.configuration.mediaAttachmentConfig.imageSizeLimit) {
                mediaAdditionError = AddMediaError(
                    AddMediaErrorType.TOO_BIG_MEDIA,
                    "Image is to big",
                    "This image is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.imageSizeLimit
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}",
                    uri,
                    metadata
                )
                return
            }
        } else if (fileType.take(5) == "video") {
            if (instance != null && instance?.configuration?.mediaAttachmentConfig?.videoSizeLimit != null && size > instance!!.configuration.mediaAttachmentConfig.videoSizeLimit!!) {
                mediaAdditionError = AddMediaError(
                    AddMediaErrorType.ERROR,
                    "Video is to big",
                    "This Video is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.videoSizeLimit!!
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}"
                )
                return
            }
        }
        val imagesNumber = mediaItems.size + 1
        if (instance != null && imagesNumber > instance!!.configuration.statusConfig.maxMediaAttachments) {
            mediaAdditionError = AddMediaError(
                AddMediaErrorType.ERROR,
                "To many images",
                "You have added to many images, your Server does only allow ${instance!!.configuration.statusConfig.maxMediaAttachments} images per post"
            )
            return
        }
        mediaItems += EditorMediaItem(
            uri,
            fileType,
            null,
            true,
            isError = false,
            locationInitialValue = null,
            metadata = metadata.copy(license = defaultLicense)
        )
        uploadImage(uri)
    }

    suspend fun compressImage(uri: KmpUri, metadata: MediaAttachmentMetadataRequest?) {
        mediaAdditionError = AddMediaError()
        compressionLoading = true
        try {

            val file = PlatformFile(uri)
            if (!fileService.exists(file)) {
                mediaAdditionError = AddMediaError(
                    AddMediaErrorType.ERROR,
                    "Unexpected Error",
                    "An unexpected Error occurred while compressing your image"
                )
                return
            }
            val imageBytes = file.readBytes()
            val compressedBytes = compressToLimit(
                imageBytes,
                instance!!.configuration.mediaAttachmentConfig.imageSizeLimit.toInt(),
                file.toImageBitmap()
            )
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val compressedFileName = "compressed_${timestamp}_${file.nameWithoutExtension}.jpg"
            val compressedFile = fileService.createTempFile(compressedFileName, compressedBytes)
            val safeUri = platform.toSafeUri(compressedFile)
            compressionLoading = false
            addImage(safeUri, metadata ?: MediaAttachmentMetadataRequest())
        } catch (exception: Throwable) {
            Logger.e(exception.message ?: "unexpected error", null, "compression")
        }
    }

    suspend fun compressToLimit(bytes: ByteArray, byteLimits: Int, bitmap: ImageBitmap): ByteArray {
        var currentBytes = bytes
        var currentMaxWidth = bitmap.width
        var currentMaxHeight = bitmap.height
        val qualityRatio = byteLimits.toDouble() / bytes.size.toDouble()
        var currentQuality = (qualityRatio * 100).toInt().coerceIn(50, 90)

        var runsCounter = 0
        Logger.i("start compression, bytes: ${bytes.size}", null, "compression")

        while (currentBytes.size > byteLimits && runsCounter < 10) {
            runsCounter++
            Logger.i(
                "Compressing: Current Size: ${currentBytes.size} vs Limit: $byteLimits (Quality: $currentQuality)",
                null,
                "compression"
            )
            try {
                currentBytes = fileService.compressImage(
                    bytes = bytes,
                    quality = currentQuality,
                    maxWidth = currentMaxWidth,
                    maxHeight = currentMaxHeight,
                    imageFormat = ImageFormat.JPEG
                )
            } catch (exception: Throwable) {
                Logger.e(exception.message ?: "unexpected error", null, "compression")
                break
            }

            if (currentBytes.size > byteLimits) {
                if (currentQuality > 50) {
                    currentQuality -= 15
                } else {
                    currentMaxWidth = (currentMaxWidth * 0.8).toInt()
                    currentMaxHeight = (currentMaxHeight * 0.8).toInt()
                    currentQuality = 70
                }
            }
        }

        return currentBytes
    }


    fun moveImage(fromIndex: Int, toIndex: Int) {
        if (fromIndex in mediaItems.indices && toIndex in mediaItems.indices) {
            val item = mediaItems.removeAt(fromIndex)
            mediaItems.add(toIndex, item)
        }
    }

    fun removeImage(index: Int) {
        if (index in mediaItems.indices) {
            mediaItems.removeAt(index)
        }
    }

    private fun uploadImage(uri: KmpUri) {
        viewModelScope.launch {
            val blurhash = createBlurHash(uri) ?: "default_or_fallback_id"

            val index = mediaItems.indexOfFirst { it.imageUri == uri }
            if (index != -1) {
                mediaItems[index] = mediaItems[index].copy(
                    metadata = mediaItems[index].metadata.copy(
                        blurhash = blurhash
                    )
                )
            }
        }

        postEditorService.uploadMedia(uri).onEach { result ->
            mediaUploadState = when (result) {
                is Resource.Success -> {
//                    if (result.data.type?.take(5) == "video") {
                    //Thread.sleep(1500) todo KMP
//                    }
                    val index = mediaItems.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        mediaItems[index] = mediaItems[index].copy(
                            isLoading = false,
                            id = result.data.id,
                            metadata = mediaItems[index].metadata.copy(
                                id = result.data.id,
                            )
                        )
                    }

                    mediaUploadState.copy(
                        mediaAttachments = mediaUploadState.mediaAttachments + result.data,
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    mediaItems.removeAll { it.imageUri == uri }
                    mediaUploadState.copy(error = result.message, isLoading = false)
                }

                is Resource.Loading -> {
                    if (mediaUploadState.error != "") {
                        mediaUploadState
                    } else {
                        mediaUploadState.copy(isLoading = true)
                    }
                }
            }
        }.flowOn(Dispatchers.io).launchIn(viewModelScope)
    }

    private suspend fun createBlurHash(uri: KmpUri): String? {
        return try {
            val file = PlatformFile(uri)
            if (!fileService.exists(file)) return null

            val bytes = file.readBytes()
            val compressedImage = fileService.compressImage(
                bytes = bytes,
                quality = 85,
                maxWidth = 50,
                maxHeight = 50,
                imageFormat = ImageFormat.PNG
            )

            BlurHashEncoder.encode(compressedImage)
        } catch (e: Exception) {
            Logger.e("Failed to create BlurHash for $uri", e)
            null
        }
    }

    private fun sortMediaUploadState(mediaUploadState: MediaUploadState): MediaUploadState {
        val sortedAttachments = mediaItems.mapNotNull { image ->
            mediaUploadState.mediaAttachments.find { it.id == image.id && it.id != null }
        }

        return mediaUploadState.copy(mediaAttachments = sortedAttachments)
    }

    private fun updateMetadata(index: Int) {
        val image = mediaItems[index]
        if (image.id == null) {
            return
        }
        postEditorService.updateMedia(image.id!!, image.metadata).onEach { result ->
            mediaUploadState = when (result) {
                is Resource.Success -> {
                    mediaUploadState.copy(
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    if (result.message.isNotEmpty()) {
                        MediaUploadState(error = result.message)
                    } else {
                        MediaUploadState(error = "An unexpected error occurred")
                    }
                }

                is Resource.Loading -> {
                    if (mediaUploadState.error != "") {
                        mediaUploadState
                    } else {
                        mediaUploadState.copy(isLoading = true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createNewPost(newMediaUploadState: MediaUploadState) {
        val mediaIds = newMediaUploadState.mediaAttachments.map { it.id }
        val locationIdNullable = locationId.ifBlank {
            null
        }
        val createPostDto = NewPostRequest(
            note = caption.text,
            mediaIds = mediaIds,
            sensitive = isSensitive,
            visibility = visibility,
            contentWarning = contentWarning,
            placeId = locationIdNullable,
            commentsDisabled = areCommentsDisabled,
            categoryId = categoriesState.selectedCategory?.id
        )
        postEditorService.createPost(createPostDto).onEach { result ->
            postSubmissionState = when (result) {
                is Resource.Success -> {
                    navigationEffectChannel.trySend(PostEditorNavigationEffect.PostCreated)
                    PostSubmissionState()
                }

                is Resource.Error -> {
                    PostSubmissionState(error = result.message)
                }

                is Resource.Loading -> {
                    PostSubmissionState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}