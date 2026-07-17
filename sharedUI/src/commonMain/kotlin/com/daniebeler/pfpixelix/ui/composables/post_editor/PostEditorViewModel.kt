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
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.Visibility
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
import com.daniebeler.pfpixelix.ui.navigation.Destination
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
    data class ImageItem(
        val imageUri: KmpUri,
        val mimeType: String?,
        var id: String?,
        var isLoading: Boolean,
        var isError: Boolean,
        var metadata: MediaAttachmentMetadataRequest
    )

    val capabilities = session.capabilities.value

    var mode by mutableStateOf(EditorMode.CREATE)
    var editingPostId: String? = null

    var images = mutableStateListOf<ImageItem>()
    var caption by mutableStateOf(TextFieldValue())
    var locationId: String by mutableStateOf("")
    var sensitive: Boolean by mutableStateOf(false)
    var sensitiveText: String by mutableStateOf("")
    var audience: Visibility by mutableStateOf(Visibility.PUBLIC)
    var disableComments: Boolean by mutableStateOf(false)
    var mediaUploadState by mutableStateOf(MediaUploadState())
    var createPostState by mutableStateOf(CreatePostState())
    var instance: Instance? = null
    var addImageError by mutableStateOf(AddMediaError())
    var compressionLoading by mutableStateOf(false)
    var accountState by mutableStateOf(AccountState())

    var isOnGeneralPage by mutableStateOf(false)
    var categoriesState by mutableStateOf(CategoriesState())
    var licensesState by mutableStateOf(LicensesState())

    private var originalContent: String = ""
    private var originalSensitive: Boolean = false
    private var originalSensitiveText: String = ""
    private var originalLocationId: String = ""
    private var originalMediaIds = listOf<String>()
    private var originalMediaDescriptions = mapOf<String, String>()

    val isEdited: Boolean by derivedStateOf {
        if (mode == EditorMode.CREATE) {
            caption.text.isNotEmpty() || images.isNotEmpty() || sensitive || sensitiveText.isNotEmpty() || locationId.isNotEmpty()
        } else {
            val currentMediaIds = images.mapNotNull { it.id }
            val currentDescriptionsChanged = images.any { image ->
                val originalDesc = originalMediaDescriptions[image.id] ?: ""
                image.metadata.description != originalDesc
            }

            caption.text != originalContent || sensitive != originalSensitive || sensitiveText != originalSensitiveText || locationId != originalLocationId || currentMediaIds != originalMediaIds || currentDescriptionsChanged
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

        userPreferences.defaultVisibilityFlow.onEach { audience = it }.launchIn(viewModelScope)
    }


    fun initForEdit(postId: String) {
        // Prevent double-initialization if already loading/loaded
        if (mode == EditorMode.EDIT && editingPostId == postId) return

        mode = EditorMode.EDIT
        editingPostId = postId
        isOnGeneralPage =
            true // Go straight to the text editor page since the post already has content

        // Reuse the existing createPostState for loading to trigger the UI loaders automatically
        createPostState = CreatePostState(isLoading = true)

        // Using postEditorService as defined in your constructor
        postService.getPostById(postId).onEach { result ->
            createPostState = when (result) {
                is Resource.Success -> {
                    val post = result.data

                    caption = TextFieldValue(post.content)
                    sensitive = post.sensitive
                    sensitiveText = post.spoilerText

                    originalContent = post.content
                    originalSensitive = post.sensitive
                    originalSensitiveText = post.spoilerText
                    originalLocationId = post.location?.id ?: ""
                    originalMediaIds = post.mediaAttachments.map { it.id }
                    originalMediaDescriptions = post.mediaAttachments.associate { it.id to (it.description ?: "") }

                    post.location?.let {
                        locationId = it.id
                    }

                    // 2. Map existing media attachments to the unified ImageItem structure
                    val mappedImages = post.mediaAttachments.map { media ->
                        ImageItem(
                            imageUri = media.url.toKmpUri(), // Coil will fetch remote URLs converted to KmpUri
                            mimeType = media.type ?: "image/jpeg",
                            id = media.id,
                            isLoading = false,
                            isError = false,
                            metadata = MediaAttachmentMetadataRequest(
                                id = media.id,
                                description = media.description ?: "",
                                blurhash = media.blurHash
                            )
                        )
                    }
                    images.clear()
                    images.addAll(mappedImages)

                    // 3. Crucial: Populate mediaUploadState so your publish/save function
                    // knows these files are already uploaded and doesn't get stuck waiting for uploads!
                    mediaUploadState = MediaUploadState(
                        mediaAttachments = post.mediaAttachments, isLoading = false
                    )

                    CreatePostState() // Success state (clears loading indicator)
                }

                is Resource.Error -> {
                    CreatePostState(error = result.message)
                }

                is Resource.Loading -> {
                    CreatePostState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    // Update your main post() orchestrator
    fun savePost(navController: NavController) {
        if (images.find { it.isLoading } != null) return // Wait for uploads

        createPostState = CreatePostState(isLoading = true)

        // Trigger metadata updates for images if they were changed
        images.forEachIndexed { index, _ -> updateMetadata(index) }

        if (mode == EditorMode.CREATE) {
            mediaUploadState = sortMediaUploadState(mediaUploadState)
            createNewPost(mediaUploadState, navController)
        } else {
            updateExistingPost(navController)
        }
    }

    fun post(navController: NavController) {
        savePost(navController)
    }

    private fun updateExistingPost(navController: NavController) {
        val postId = editingPostId ?: return
        val mediaIds = images.mapNotNull { it.id }
        val locationIdNullable = locationId.ifBlank { null }

        val updateRequest = NewPostRequest(
            note = caption.text,
            mediaIds = mediaIds,
            sensitive = sensitive,
            visibility = audience,
            contentWarning = sensitiveText,
            placeId = locationIdNullable,
            commentsDisabled = disableComments,
            categoryId = categoriesState.selectedCategory?.id
        )

        postEditorService.updatePost(postId, updateRequest).onEach { result ->
            createPostState = when (result) {
                is Resource.Success -> {
                    // Navigate back to the updated post details directly
                    navController.popBackStack()
                    navController.navigate(Destination.Post(postId, refresh = true)) {
                        launchSingleTop = true
                    }
                    CreatePostState()
                }

                is Resource.Error -> {
                    CreatePostState(error = result.message)
                }

                is Resource.Loading -> {
                    CreatePostState(isLoading = true)
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
        images = images.also {
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
            addImageError = AddMediaError(
                AddMediaErrorType.ERROR,
                "Media type is not supported",
                "The media type $fileType is not supported by this server"
            )
            return
        }
        val size = file.size()

        if (fileType.take(5) == "image") {
            if (instance != null && size > instance!!.configuration.mediaAttachmentConfig.imageSizeLimit) {
                addImageError = AddMediaError(
                    AddMediaErrorType.TOO_BIG_MEDIA,
                    "Image is to big",
                    "This image is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.imageSizeLimit
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}",
                    uri
                )
                return
            }
        } else if (fileType.take(5) == "video") {
            if (instance != null && instance?.configuration?.mediaAttachmentConfig?.videoSizeLimit != null && size > instance!!.configuration.mediaAttachmentConfig.videoSizeLimit!!) {
                addImageError = AddMediaError(
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
        val imagesNumber = images.size + 1
        if (instance != null && imagesNumber > instance!!.configuration.statusConfig.maxMediaAttachments) {
            addImageError = AddMediaError(
                AddMediaErrorType.ERROR,
                "To many images",
                "You have added to many images, your Server does only allow ${instance!!.configuration.statusConfig.maxMediaAttachments} images per post"
            )
            return
        }
        images += ImageItem(uri, fileType, null, true, isError = false, metadata = metadata)
        uploadImage(uri)
    }

    suspend fun compressImage(uri: KmpUri) {
        addImageError = AddMediaError()
        compressionLoading = true
        try {

            val file = PlatformFile(uri)
            if (!fileService.exists(file)) {
                addImageError = AddMediaError(
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
            //TODO: fix compress, (metadata has to be kept the same)
            addImage(safeUri, MediaAttachmentMetadataRequest())
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
        if (fromIndex in images.indices && toIndex in images.indices) {
            val item = images.removeAt(fromIndex)
            images.add(toIndex, item)
        }
    }

    fun removeImage(index: Int) {
        if (index in images.indices) {
            images.removeAt(index)
        }
    }

    private fun uploadImage(uri: KmpUri) {
        viewModelScope.launch {
            val blurhash = createBlurHash(uri) ?: "default_or_fallback_id"

            val index = images.indexOfFirst { it.imageUri == uri }
            if (index != -1) {
                images[index] = images[index].copy(
                    metadata = images[index].metadata.copy(
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
                    val index = images.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        images[index] = images[index].copy(
                            isLoading = false,
                            id = result.data.id,
                            metadata = images[index].metadata.copy(
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
                    val index = images.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        images[index] = images[index].copy(
                            isLoading = false, isError = true
                        )
                    }
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
        var newMediaUploadState = MediaUploadState()
        images.forEach { image ->
            newMediaUploadState =
                newMediaUploadState.copy(mediaAttachments = newMediaUploadState.mediaAttachments + mediaUploadState.mediaAttachments.find { it.id == image.id }!!)
        }

        return newMediaUploadState
    }

    private fun updateMetadata(index: Int) {
        val image = images[index]
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

    private fun createNewPost(newMediaUploadState: MediaUploadState, navController: NavController) {
        val mediaIds = newMediaUploadState.mediaAttachments.map { it.id }
        val locationIdNullable = locationId.ifBlank {
            null
        }
        val createPostDto = NewPostRequest(
            note = caption.text,
            mediaIds = mediaIds,
            sensitive = sensitive,
            visibility = audience,
            contentWarning = sensitiveText,
            placeId = locationIdNullable,
            commentsDisabled = disableComments,
            categoryId = categoriesState.selectedCategory?.id
        )
        postEditorService.createPost(createPostDto).onEach { result ->
            createPostState = when (result) {
                is Resource.Success -> {
                    navController.navigate(Destination.HomeTabOwnProfile) {
                        restoreState = false
                        popUpTo<Destination.HomeTabNewPost> {
                            inclusive = true
                        }
                    }
                    CreatePostState()
                }

                is Resource.Error -> {
                    CreatePostState(error = result.message)
                }

                is Resource.Loading -> {
                    CreatePostState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}