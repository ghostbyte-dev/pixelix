package com.daniebeler.pfpixelix.widget
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.ByteArrayOutputStream
import android.util.Base64
import androidx.annotation.RequiresApi

object BitmapSerializer : KSerializer<Bitmap?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Bitmap", PrimitiveKind.STRING)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun serialize(encoder: Encoder, value: Bitmap?) {
        if (value == null) {
            encoder.encodeString("")
            return
        }
        val outputStream = ByteArrayOutputStream()
        // Use WEBP or JPEG to keep the size small for the widget
        value.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        encoder.encodeString(Base64.encodeToString(byteArray, Base64.DEFAULT))
    }

    override fun deserialize(decoder: Decoder): Bitmap? {
        val base64String = decoder.decodeString()
        if (base64String.isEmpty()) return null
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}