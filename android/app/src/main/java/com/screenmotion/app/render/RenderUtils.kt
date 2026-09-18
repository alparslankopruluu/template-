package com.screenmotion.app.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.max

fun loadAssetBitmap(context: Context, path: String?): Bitmap? = path?.let {
    runCatching { context.assets.open(it).use(BitmapFactory::decodeStream) }.getOrNull()
}

fun Canvas.drawCenterCrop(bitmap: Bitmap?, offsetX: Float = 0f, offsetY: Float = 0f, zoom: Float = 1.06f, paint: Paint? = null) {
    if (bitmap == null || width <= 0 || height <= 0) return
    val scale = max(width / bitmap.width.toFloat(), height / bitmap.height.toFloat()) * zoom
    val dw = bitmap.width * scale
    val dh = bitmap.height * scale
    val left = (width - dw) / 2f + offsetX
    val top = (height - dh) / 2f + offsetY
    drawBitmap(bitmap, null, RectF(left, top, left + dw, top + dh), paint)
}
