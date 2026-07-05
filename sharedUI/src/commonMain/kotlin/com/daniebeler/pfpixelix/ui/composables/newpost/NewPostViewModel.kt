package com.daniebeler.pfpixelix.ui.composables.newpost

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
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.AccountState
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.resolve
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Clock


class NewPostViewModel @Inject constructor(
    private val postEditorService: PostEditorService,
    private val instanceService: InstanceService,
    private val fileService: FileService,
    private val platform: Platform,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager,
    private val accountService: AccountService,
    private val session: Session,
    private val userPreferences: UserPreferences
) : ViewModel() {
    data class ImageItem(
        val imageUri: KmpUri,
        val mimeType: String,
        var id: String?,
        var isLoading: Boolean,
        var metadata: MediaAttachmentMetadataRequest
    )

    val capabilities = session.capabilities.value

    var images = mutableStateListOf<ImageItem>()
    var caption by mutableStateOf(TextFieldValue())
    var locationId: String by mutableStateOf("")
    var sensitive: Boolean by mutableStateOf(false)
    var sensitiveText: String by mutableStateOf("")
    var audience: Visibility by mutableStateOf(Visibility.PUBLIC)
    var mediaUploadState by mutableStateOf(MediaUploadState())
    var createPostState by mutableStateOf(CreatePostState())
    var instance: Instance? = null
    var addImageError by mutableStateOf(AddMediaError())
    var compressionLoading by mutableStateOf(false)
    var accountState by mutableStateOf(AccountState())

    init {
        viewModelScope.launch {
            getInstance()
            getAccount()
        }

        userPreferences.captionTemplateFlow
            .onEach { caption = TextFieldValue(it) }
            .launchIn(viewModelScope)

        userPreferences.defaultVisibilityFlow
            .onEach { audience = it }
            .launchIn(viewModelScope)
    }

    fun updateCaption(newCaption: TextFieldValue) {
        caption = newCaption
        hashtagMentionsSuggestionsManager.changeText(newCaption, viewModelScope)
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
        return if (bytes >= 0 && bytes < kilobyte) {
            "$bytes B"
        } else if (bytes >= kilobyte && bytes < megabyte) {
            (bytes / kilobyte).toString() + " KB"
        } else if (bytes >= megabyte && bytes < gigabyte) {
            (bytes / megabyte).toString() + " MB"
        } else if (bytes >= gigabyte && bytes < terabyte) {
            (bytes / gigabyte).toString() + " GB"
        } else if (bytes >= terabyte) {
            (bytes / terabyte).toString() + " TB"
        } else {
            "$bytes Bytes"
        }
    }

    fun addImage(uri: KmpUri, metadata: MediaAttachmentMetadataRequest) {
        val file = PlatformFile(uri)
        if (!file.exists()) {
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
                "The media type $fileType is not supportet by this server"
            )
            return
        }
        val size = file.size()

        if (fileType.take(5) == "image") {
            if (instance != null && size > instance!!.configuration.mediaAttachmentConfig.imageSizeLimit) {
                addImageError = AddMediaError(
                    AddMediaErrorType.TOO_BIG_MEDIA,
                    "Image is to big", "This image is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.imageSizeLimit
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}", uri
                )
                return
            }
        } else if (fileType.take(5) == "video") {
            if (instance != null && instance?.configuration?.mediaAttachmentConfig?.videoSizeLimit != null && size > instance!!.configuration.mediaAttachmentConfig.videoSizeLimit!!) {
                addImageError = AddMediaError(
                    AddMediaErrorType.ERROR,
                    "Video is to big", "This Video is to big, the max size for this server is ${
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
        images += ImageItem(uri, fileType, null, true, metadata)
        uploadImage(uri)
    }

    suspend fun compressImage(uri: KmpUri) {
        addImageError = AddMediaError()
        compressionLoading = true
        try {

            val file = PlatformFile(uri)
            if (!file.exists()) {
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
            val compressedFile = FileKit.cacheDir.resolve(compressedFileName)
            compressedFile.write(compressedBytes)
            val safeUri = platform.toSafeUri(compressedFile)
            compressionLoading = false
            //TODO: fix compress, (metadata has to be kept the same)
            addImage(safeUri, MediaAttachmentMetadataRequest())
        } catch (exception: Exception) {
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
                currentBytes = FileKit.compressImage(
                    bytes = bytes,
                    quality = currentQuality,
                    maxWidth = currentMaxWidth,
                    maxHeight = currentMaxHeight,
                    imageFormat = ImageFormat.JPEG
                )
            } catch (exception: Exception) {
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

    fun deleteMedia(index: Int) {
        images.removeAt(index)
    }

    fun moveMediaAttachmentUp(index: Int) {
        if (index >= 1) {
            val copy = images[index]
            images[index] = images[index - 1]
            images[index - 1] = copy
        }
    }

    fun moveMediaAttachmentDown(index: Int) {
        if (index < images.size - 1) {
            val copy = images[index]
            images[index] = images[index + 1]
            images[index + 1] = copy
        }
    }

    private fun uploadImage(uri: KmpUri) {
        postEditorService.uploadMedia(uri).onEach { result ->
            mediaUploadState = when (result) {
                is Resource.Success -> {
                    if (result.data.type?.take(5) == "video") {
                        //Thread.sleep(1500) todo KMP
                    }
                    val index = images.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        images[index] = images[index].copy(
                            isLoading = false,
                            id = result.data.id,
                            metadata = images[index].metadata.copy(id = result.data.id)
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
                        images.removeAt(index)
                    }
                    MediaUploadState(error = result.message)
                }

                is Resource.Loading -> {
                    if (mediaUploadState.error != "") {
                        mediaUploadState
                    } else {
                        mediaUploadState.copy(isLoading = true)
                    }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun post(navController: NavController) {
        if (images.find { it.isLoading } != null && images.isEmpty()) {
            return
        }
        createPostState = CreatePostState(isLoading = true)
        if (images.size == mediaUploadState.mediaAttachments.size) {
            images.forEachIndexed { index, it ->
                //TODO: check if metadata has changed from default empty metadata
                updateMetadata(index, it.metadata)
            }
            mediaUploadState = sortMediaUploadState(mediaUploadState)
            createNewPost(mediaUploadState, navController)
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

    private fun updateMetadata(index: Int, metadata: MediaAttachmentMetadataRequest) {
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
                        MediaUploadState(error = "An unexpected error occured")
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
        val createPostDto =
            NewPostRequest(
                note = caption.text,
                mediaIds = mediaIds,
                sensitive = sensitive,
                visibility = audience,
                contentWarning = sensitiveText,
                placeId = locationIdNullable,
                commentsDisabled = false,
                categoryId = null
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
                    CreatePostState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    CreatePostState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setLocation(id: String) {
        locationId = id
    }
}