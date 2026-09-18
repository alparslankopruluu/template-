package com.screenmotion.app.render

import android.content.Context
import android.graphics.*
import com.screenmotion.app.model.ThemeConfig
import kotlin.math.exp

class VehicleSceneRenderer(context: Context, private val config: ThemeConfig) : SceneRenderer {
    private val background = loadAssetBitmap(context, config.type.assetPath)
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply { maskFilter = BlurMaskFilter(22f, BlurMaskFilter.Blur.NORMAL) }
    private val body = Paint(Paint.ANTI_ALIAS_FLAG)
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD }
    private var w = 0f; private var h = 0f; private var roll = 0f; private var pitch = 0f; private var x = 0f; private var velocity = 0f; private var touchTarget: Float? = null
    override fun onSizeChanged(width: Int, height: Int) { w = width.toFloat(); h = height.toFloat(); x = w / 2f }
    override fun onMotion(roll: Float, pitch: Float) { if (config.gyroEnabled) { this.roll = roll; this.pitch = pitch } }
    override fun onTouch(x: Float, y: Float, down: Boolean) { touchTarget = if (config.touchEnabled && down) x else null }
    override fun update(dt: Float) { if (w <= 0) return; val gyroTarget = w / 2f + roll * w * 0.26f * config.parallaxStrength; val target = touchTarget ?: gyroTarget; val force = (target - x) * 9f; velocity += force * dt; velocity *= exp(-5.5f * dt); x += velocity * dt; x = x.coerceIn(w * .16f, w * .84f) }
    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(5, 9, 18)); canvas.drawCenterCrop(background, offsetX = -roll * 34f, offsetY = -pitch * 20f, zoom = 1.09f); if (w == 0f) return
        val y = h * .76f; val size = (w * .16f).coerceAtMost(190f); val normalized = ((x - w / 2f) / (w / 2f)).coerceIn(-1f,1f); canvas.save(); canvas.rotate(-normalized * 7f, x, y)
        glow.color = Color.argb(170, 120, 50, 255); canvas.drawOval(RectF(x-size*1.15f, y-size*.15f, x+size*1.15f, y+size*.55f), glow)
        body.color = when (config.type.name) { "TANK" -> Color.rgb(84,102,70); "TRACTOR" -> Color.rgb(229,176,30); "BUS" -> Color.rgb(65,130,240); "TRUCK" -> Color.rgb(235,78,54); "HELICOPTER", "AIRPLANE" -> Color.rgb(215,220,230); "BICYCLE" -> Color.rgb(70,225,190); else -> Color.rgb(225,35,78) }
        when (config.type.name) {
            "AIRPLANE" -> { val p = Path().apply { moveTo(x, y-size*.8f); lineTo(x+size*.22f,y); lineTo(x+size,y+size*.25f); lineTo(x+size*.18f,y+size*.18f); lineTo(x,y+size*.65f); lineTo(x-size*.18f,y+size*.18f); lineTo(x-size,y+size*.25f); lineTo(x-size*.22f,y); close() }; canvas.drawPath(p, body) }
            "HELICOPTER" -> { canvas.drawRoundRect(RectF(x-size*.8f,y-size*.25f,x+size*.55f,y+size*.3f), size*.22f,size*.22f,body); body.strokeWidth = 10f; canvas.drawLine(x-size*.05f,y-size*.3f,x+size*.95f,y-size*.3f,body); canvas.drawLine(x+size*.5f,y,x+size*1.15f,y-size*.2f,body) }
            "BICYCLE" -> { body.style = Paint.Style.STROKE; body.strokeWidth = size*.08f; canvas.drawCircle(x-size*.55f,y+size*.15f,size*.35f,body); canvas.drawCircle(x+size*.55f,y+size*.15f,size*.35f,body); canvas.drawLine(x-size*.55f,y+size*.15f,x,y-size*.25f,body); canvas.drawLine(x,y-size*.25f,x+size*.2f,y+size*.15f,body); canvas.drawLine(x+size*.2f,y+size*.15f,x-size*.55f,y+size*.15f,body); canvas.drawLine(x+size*.2f,y+size*.15f,x+size*.55f,y+size*.15f,body); body.style = Paint.Style.FILL }
            else -> { canvas.drawRoundRect(RectF(x-size,y-size*.35f,x+size,y+size*.35f), size*.22f,size*.22f,body); body.color = Color.argb(220,20,25,35); canvas.drawRoundRect(RectF(x-size*.55f,y-size*.62f,x+size*.45f,y-size*.15f), size*.16f,size*.16f,body); body.color = Color.rgb(18,18,22); canvas.drawCircle(x-size*.58f,y+size*.35f,size*.22f,body); canvas.drawCircle(x+size*.58f,y+size*.35f,size*.22f,body) }
        }
        canvas.restore(); text.textSize = (w * .034f).coerceIn(24f,45f); text.alpha = 220; canvas.drawText("${config.type.emoji} ${config.type.title} • tilt / drag", w/2f, h*.94f, text)
    }
    override fun release() { background?.recycle() }
}
