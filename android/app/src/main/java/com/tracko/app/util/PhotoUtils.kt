package com.tracko.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoUtils {

    private const val MAX_IMAGE_SIZE = 1024
    private const val COMPRESSION_QUALITY = 80

    fun compressBitmap(inputPath: String, maxSize: Int = MAX_IMAGE_SIZE): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(inputPath, options)

            var scale = 1
            while (options.outWidth / scale / 2 >= maxSize &&
                options.outHeight / scale / 2 >= maxSize
            ) {
                scale *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            BitmapFactory.decodeFile(inputPath, decodeOptions)
        } catch (e: Exception) {
            null
        }
    }

    fun generateWatermarkedPhoto(
        bitmap: Bitmap,
        timestamp: String,
        latitude: Double?,
        longitude: Double?,
        employeeName: String?
    ): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val paint = Paint().apply {
            color = Color.WHITE
            textSize = result.width * 0.04f
            isAntiAlias = true
            setShadowLayer(2f, 1f, 1f, Color.BLACK)
        }

        val bottomPaint = Paint().apply {
            color = Color.argb(180, 0, 0, 0)
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = Color.argb(180, 0, 0, 0)
            isAntiAlias = true
        }

        val headerHeight = result.height * 0.08f
        canvas.drawRect(RectF(0f, 0f, result.width.toFloat(), headerHeight), headerPaint)

        paint.textSize = result.width * 0.035f
        paint.color = Color.WHITE
        val headerText = "Tracko - $employeeName"
        canvas.drawText(headerText, 10f, headerHeight - 10f, paint)

        val footerHeight = result.height * 0.06f
        canvas.drawRect(
            RectF(0f, result.height - footerHeight, result.width.toFloat(), result.height.toFloat()),
            bottomPaint
        )

        paint.textSize = result.width * 0.03f
        val locationStr = if (latitude != null && longitude != null) {
            "Lat: $latitude, Lng: $longitude"
        } else {
            "Location: N/A"
        }
        canvas.drawText(timestamp, 10f, result.height - footerHeight + 20f, paint)
        canvas.drawText(locationStr, 10f, result.height - footerHeight + 45f, paint)

        return result
    }

    fun saveToCache(context: Context, bitmap: Bitmap, prefix: String = "photo"): Uri? {
        return try {
            val dir = File(context.cacheDir, "photos")
            if (!dir.exists()) dir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(dir, "${prefix}_$timestamp.jpg")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }

            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream)
        val byteArray = stream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    fun createTempImageFile(context: Context): File {
        val dir = File(context.cacheDir, "photos")
        if (!dir.exists()) dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(dir, "IMG_$timestamp.jpg")
    }
}
