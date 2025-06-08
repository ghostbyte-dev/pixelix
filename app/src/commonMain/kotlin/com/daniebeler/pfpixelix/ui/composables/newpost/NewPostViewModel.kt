package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.NewPost
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.service.editor.PostEditorService
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.instance.InstanceService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject


class NewPostViewModel @Inject constructor(
    private val postEditorService: PostEditorService,
    private val instanceService: InstanceService,
    private val fileService: FileService
) : ViewModel() {
    data class ImageItem(
        val imageUri: KmpUri,
        val mimeType: String,
        var id: String?,
        var text: String = "",
        var isLoading: Boolean
    )

    var images = mutableStateListOf<ImageItem>()
    var caption: String by mutableStateOf("")
    private var locationId: String by mutableStateOf("")
    var sensitive: Boolean by mutableStateOf(false)
    var sensitiveText: String by mutableStateOf("")
    var audience: Visibility by mutableStateOf(Visibility.PUBLIC)
    var mediaUploadState by mutableStateOf(MediaUploadState())
    var createPostState by mutableStateOf(CreatePostState())
    var instance: Instance? = null
    var addImageError by mutableStateOf(Pair("", ""))

    init {
        getInstance()
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

    fun updateAltTextVariable(index: Int, newAltText: String) {
        images = images.also {
            it[index] = it[index].copy(text = newAltText)
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

    fun addImage(uri: KmpUri) {
        val file = PlatformFile(uri)
        if (!file.exists()) return
        val fileType = fileService.getMimeType(file)
        if (instance != null && !instance!!.configuration.mediaAttachmentConfig.supportedMimeTypes.contains(
                fileType
            )
        ) {
            addImageError = Pair(
                "Media type is not supported",
                "The media type $fileType is not supportet by this server"
            )
            return
        }
        val size = file.size()

        if (fileType.take(5) == "image") {
            if (instance != null && size > instance!!.configuration.mediaAttachmentConfig.imageSizeLimit) {
                addImageError = Pair(
                    "Image is to big", "This image is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.imageSizeLimit.toLong()
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}"
                )
                return
            }
        } else if (fileType.take(5) == "video") {
            if (instance != null && size > instance!!.configuration.mediaAttachmentConfig.videoSizeLimit) {
                addImageError = Pair(
                    "Video is to big", "This Video is to big, the max size for this server is ${
                        bytesIntoHumanReadable(
                            instance!!.configuration.mediaAttachmentConfig.videoSizeLimit.toLong()
                        )
                    }, your video has ${bytesIntoHumanReadable(size)}"
                )
                return
            }
        }
        val imagesNumber = images.size + 1
        if (instance != null && imagesNumber > instance!!.configuration.statusConfig.maxMediaAttachments) {
            addImageError = Pair(
                "To many images",
                "You have added to many images, your Server does only allow ${instance!!.configuration.statusConfig.maxMediaAttachments} images per post"
            )
            return
        }
        images += ImageItem(uri, fileType, null, "", true)
        uploadImage(uri, "")
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

    private fun uploadImage(uri: KmpUri, text: String) {
        postEditorService.uploadMedia(uri, text).onEach { result ->
            mediaUploadState = when (result) {
                is Resource.Success -> {
                    if (result.data?.type?.take(5) == "video") {
                        //Thread.sleep(1500) todo KMP
                    }
                    val index = images.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        images[index] = images[index].copy(
                            isLoading = false,
                            id = result.data?.id
                        ) // Replacing the object forces recomposition
                    }

                    mediaUploadState.copy(
                        mediaAttachments = mediaUploadState.mediaAttachments + result.data!!,
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    val index = images.indexOfFirst { it.imageUri == uri }
                    if (index != -1) {
                        images.removeAt(index)
                    }
                    MediaUploadState(error = "An unexpected error occured")
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
                if (it.text != "") {
                    updateAltText(index, it.text)
                }
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

    private fun updateAltText(index: Int, altText: String) {
        val image = images[index]
        if (image.id == null) {
            return
        }
        postEditorService.updateMedia(image.id!!, image.text).onEach { result ->
            mediaUploadState = when (result) {
                is Resource.Success -> {
                    images.find { it.imageUri == image.imageUri }?.isLoading = false
                    var index: Int = 0
                    mediaUploadState.mediaAttachments.forEachIndexed { i, it ->
                        if (it.id == image.id) {
                            index = i
                        }
                    }
                    mediaUploadState.copy(
                        mediaAttachments = mediaUploadState.mediaAttachments.toMutableList().also {
                            it[index] = result.data!!
                        }, isLoading = false
                    )
                }

                is Resource.Error -> {
                    if (!result.message.isNullOrEmpty()) {
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
        val locationIdNullable = if (locationId.isBlank()) {
            null
        } else {
            locationId
        }
        val createPostDto =
            NewPost(caption, mediaIds, sensitive, audience, sensitiveText, locationIdNullable)
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