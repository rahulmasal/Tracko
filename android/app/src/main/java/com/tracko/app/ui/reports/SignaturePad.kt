package com.tracko.app.ui.reports

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path as ComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignaturePad(
    onSignatureSaved: (java.net.URI) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf<ComposePath?>(null) }
    var paths by remember { mutableStateOf(listOf<ComposePath>()) }
    var isDrawing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.firstOrNull() ?: break

                        when {
                            pointer.pressed && !isDrawing -> {
                                isDrawing = true
                                currentPath = ComposePath().apply {
                                    moveTo(pointer.position.x, pointer.position.y)
                                }
                            }
                            pointer.pressed && isDrawing -> {
                                currentPath?.lineTo(pointer.position.x, pointer.position.y)
                            }
                            !pointer.pressed && isDrawing -> {
                                isDrawing = false
                                currentPath?.let { paths = paths + it }
                                currentPath = null
                            }
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val paint = Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            paths.forEach { path ->
                drawPath(path, color = androidx.compose.ui.graphics.Color.Black, style = paint)
            }
            currentPath?.let { path ->
                drawPath(path, color = androidx.compose.ui.graphics.Color.Black, style = paint)
            }
        }

        if (paths.isEmpty()) {
            Text(
                text = "Sign here",
                color = Color.GRAY,
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                paths = emptyList()
                currentPath = null
            }
        ) {
            Icon(Icons.Default.Clear, contentDescription = "Clear signature")
        }
        IconButton(
            onClick = {
                if (paths.isNotEmpty()) {
                    val bitmap = Bitmap.createBitmap(800, 300, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.WHITE)
                    val paint = Paint().apply {
                        color = Color.BLACK
                        strokeWidth = 8f
                        style = Paint.Style.STROKE
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                        isAntiAlias = true
                    }
                    val androidPath = Path()
                    paths.forEach { composePath ->
                        val pathString = composePath.toString()
                        androidPath.lineTo(0f, 0f)
                    }
                    canvas.drawPath(androidPath, paint)

                    val dir = File(context.cacheDir, "signatures")
                    if (!dir.exists()) dir.mkdirs()
                    val file = File(dir, "signature_${System.currentTimeMillis()}.png")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    onSignatureSaved(uri.toURI())
                }
            }
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save signature")
        }
    }
}
