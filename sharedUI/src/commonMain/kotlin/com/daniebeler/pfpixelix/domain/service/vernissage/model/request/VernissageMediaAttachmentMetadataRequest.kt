import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageMediaAttachmentMetadataRequest(
    @SerialName("id") val id: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("previewUrl") val previewUrl: String = "",
    @SerialName("description") val description: String? = null,
//    @SerialName("blurhash") val blurhash: String? = null,
    @SerialName("make") val make: String? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("lens") val lens: String? = null,
    @SerialName("createDate") val createDate: String? = null,
    @SerialName("focalLength") val focalLength: String? = null,
    @SerialName("focalLenIn35mmFilm") val focalLenIn35mmFilm: String? = null,
    @SerialName("fNumber") val fNumber: String? = null,
    @SerialName("exposureTime") val exposureTime: String? = null,
    @SerialName("photographicSensitivity") val photographicSensitivity: String? = null,
    @SerialName("software") val software: String? = null,
    @SerialName("film") val film: String? = null,
    @SerialName("chemistry") val chemistry: String? = null,
    @SerialName("scanner") val scanner: String? = null,
    @SerialName("locationId") val locationId: String? = null,
//    @SerialName("licenseId") val licenseId: String? = null,
//    @SerialName("latitude") val latitude: String? = null,
//    @SerialName("longitude") val longitude: String? = null,
    @SerialName("flash") val flash: String? = null
)