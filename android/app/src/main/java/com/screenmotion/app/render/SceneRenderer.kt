package com.screenmotion.app.render

import android.graphics.Canvas

interface SceneRenderer {
    fun onSizeChanged(width: Int, height: Int)
    fun onMotion(roll: Float, pitch: Float)
    fun onTouch(x: Float, y: Float, down: Boolean)
    fun update(dt: Float)
    fun draw(canvas: Canvas)
    fun release() = Unit
}
