package com.screenmotion.app.wallpaper

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.screenmotion.app.data.ConfigRepository
import com.screenmotion.app.motion.MotionController
import com.screenmotion.app.render.RendererFactory
import com.screenmotion.app.render.SceneRenderer

class InteractiveWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = MotionEngine()

    inner class MotionEngine : Engine() {
        private val repository by lazy { ConfigRepository(applicationContext) }
        private var renderer: SceneRenderer? = null
        private var motionController: MotionController? = null
        private var renderThread: HandlerThread? = null
        private var renderHandler: Handler? = null
        private var visible = false
        private var width = 0
        private var height = 0
        private var lastFrameNanos = 0L
        private var frameDelayMs = 33L
        private var gyroEnabled = true

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
            rebuildScene()
        }

        private fun rebuildScene() {
            motionController?.stop()
            renderer?.release()

            val config = repository.load()
            gyroEnabled = config.gyroEnabled
            frameDelayMs = if (config.fps >= 60) 16L else 33L

            renderer = RendererFactory.create(applicationContext, config).also {
                if (width > 0 && height > 0) it.onSizeChanged(width, height)
            }

            motionController = MotionController(applicationContext) { roll, pitch ->
                renderer?.onMotion(roll, pitch)
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height
            renderer?.onSizeChanged(width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible

            if (visible) {
                rebuildScene()
                if (gyroEnabled) motionController?.start()
                startRenderLoop()
            } else {
                motionController?.stop()
                stopRenderLoop()
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            renderer?.onTouch(
                event.x,
                event.y,
                event.actionMasked != MotionEvent.ACTION_UP &&
                    event.actionMasked != MotionEvent.ACTION_CANCEL
            )
            super.onTouchEvent(event)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            motionController?.stop()
            stopRenderLoop()
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            stopRenderLoop()
            motionController?.stop()
            renderer?.release()
            renderer = null
            super.onDestroy()
        }

        private fun startRenderLoop() {
            if (renderThread != null) return

            renderThread = HandlerThread(
                "ScreenMotionWallpaper",
                Process.THREAD_PRIORITY_DISPLAY
            ).also { it.start() }

            renderHandler = Handler(renderThread!!.looper)
            lastFrameNanos = System.nanoTime()
            renderHandler?.post(frameRunnable)
        }

        private fun stopRenderLoop() {
            renderHandler?.removeCallbacksAndMessages(null)
            renderThread?.quitSafely()
            renderThread = null
            renderHandler = null
        }

        private val frameRunnable = object : Runnable {
            override fun run() {
                if (!visible) return

                val now = System.nanoTime()
                val dt = ((now - lastFrameNanos) / 1_000_000_000f).coerceIn(0f, .05f)
                lastFrameNanos = now

                renderer?.update(dt)
                drawFrame()

                renderHandler?.postDelayed(this, frameDelayMs)
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            if (!holder.surface.isValid) return

            var canvas: android.graphics.Canvas? = null
            try {
                canvas = holder.lockCanvas()
                canvas?.let { renderer?.draw(it) }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
        }
    }
}
