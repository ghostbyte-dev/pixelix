package com.daniebeler.pfpixelix.widget.latest_image.utils

import android.content.Context
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.glance.ImageProvider

class GetImageProvider {
    operator fun invoke(imagePath: String, context: Context, radius: Float): ImageProvider {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val listener: ImageDecoder.OnHeaderDecodedListener =
                    ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->
                        if (info.size.width > 1400 || info.size.height > 1400) {
                            decoder.setTargetSampleSize(3)
                        } else if (info.size.width > 800  || info.size.height > 800) {
                            decoder.setTargetSampleSize(2)
                        }
                        decoder.setPostProcessor { canvas ->
                            // This will create rounded corners.
                            val path = Path()
                            path.setFillType(Path.FillType.INVERSE_EVEN_ODD)
                            val width: Float = canvas.getWidth().toFloat()
                            val height: Float = canvas.getHeight().toFloat()
                            path.addRoundRect(0f, 0f, width, height, radius, radius, Path.Direction.CW)
                            val paint = Paint()
                            paint.isAntiAlias = true
                            paint.setColor(Color.TRANSPARENT)
                            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC))
                            canvas.drawPath(path, paint)
                            PixelFormat.TRANSLUCENT
                        }
                    }
                val imageSource: ImageDecoder.Source =
                    ImageDecoder.createSource(context.contentResolver, imagePath.toUri())
                return ImageProvider(ImageDecoder.decodeBitmap(imageSource, listener))
            } catch (e: Exception) {
                return oldWayToGetBitmap(context, imagePath, radius)
            }
        }
        return oldWayToGetBitmap(context, imagePath, radius)
    }

    private fun oldWayToGetBitmap(context: Context, imagePath: String, radius: Float): ImageProvider {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imagePath.toUri())
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        roundedBitmapDrawable.cornerRadius = radius.dp.value
        return ImageProvider(roundedBitmapDrawable.current.toBitmap())
    }

}