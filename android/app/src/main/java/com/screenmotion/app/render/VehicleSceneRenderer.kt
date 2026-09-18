package com.screenmotion.app.render

import android.content.Context
import android.graphics.*
import com.screenmotion.app.model.ThemeConfig
import kotlin.math.exp

class VehicleSceneRenderer(
    context: Context,
    private val config: ThemeConfig
) : SceneRenderer {

    private val background = loadAssetBitmap(context, config.type.assetPath)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(22f, BlurMaskFilter.Blur.NORMAL)
    }
    private val body = Paint(Paint.ANTI_ALIAS_FLAG)
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private var w = 0f
    private var h = 0f
    private var roll = 0f
    private var pitch = 0f
    private var x = 0f
    private var velocity = 0f
    private var touchTarget: Float? = null

    override fun onSizeChanged(width: Int, height: Int) {
        w = width.toFloat()
        h = height.toFloat()
        x = w / 2f
    }

    override fun onMotion(roll: Float, pitch: Float) {
        if (config.gyroEnabled) {
            this.roll = roll
            this.pitch = pitch
        }
    }

    override fun onTouch(x: Float, y: Float, down: Boolean) {
        touchTarget = if (config.touchEnabled && down) x else null
    }

    override fun update(dt: Float) {
        if (w <= 0) return

        val gyroTarget = w / 2f + roll * w * .26f * config.parallaxStrength
        val target = touchTarget ?: gyroTarget
        val force = (target - x) * 9f

        velocity += force * dt
        velocity *= exp(-5.5f * dt)
        x += velocity * dt
        x = x.coerceIn(w * .14f, w * .86f)
    }

    override fun draw(canvas: Canvas) {
        if (w <= 0f || h <= 0f) return

        drawProceduralBackground(canvas)
        canvas.drawCenterCrop(
            background,
            offsetX = -roll * 34f,
            offsetY = -pitch * 20f,
            zoom = 1.09f
        )

        val flying = config.type.name in setOf("AIRPLANE", "HELICOPTER", "SPACESHIP")
        val y = if (flying) h * .58f else h * .76f
        val size = (w * .16f).coerceAtMost(190f)
        val normalized = ((x - w / 2f) / (w / 2f)).coerceIn(-1f, 1f)

        canvas.save()
        canvas.rotate(-normalized * 7f, x, y)

        glow.color = when (config.type.name) {
            "SPACESHIP" -> Color.argb(200, 55, 190, 255)
            "AIRPLANE", "HELICOPTER" -> Color.argb(145, 130, 190, 255)
            "TANK" -> Color.argb(160, 120, 160, 90)
            else -> Color.argb(170, 120, 50, 255)
        }

        canvas.drawOval(
            RectF(
                x - size * 1.15f,
                y - size * .15f,
                x + size * 1.15f,
                y + size * .55f
            ),
            glow
        )

        body.color = when (config.type.name) {
            "TANK" -> Color.rgb(84, 102, 70)
            "TRACTOR" -> Color.rgb(229, 176, 30)
            "BUS" -> Color.rgb(65, 130, 240)
            "TRUCK" -> Color.rgb(235, 78, 54)
            "HELICOPTER", "AIRPLANE", "SPACESHIP" -> Color.rgb(215, 225, 235)
            "BICYCLE" -> Color.rgb(70, 225, 190)
            else -> Color.rgb(225, 35, 78)
        }

        when (config.type.name) {
            "SPACESHIP" -> drawSpaceship(canvas, x, y, size)
            "AIRPLANE" -> drawAirplane(canvas, x, y, size)
            "HELICOPTER" -> drawHelicopter(canvas, x, y, size)
            "BICYCLE" -> drawBicycle(canvas, x, y, size)
            "TANK" -> drawTank(canvas, x, y, size)
            else -> drawRoadVehicle(canvas, x, y, size)
        }

        canvas.restore()

        text.textSize = (w * .034f).coerceIn(24f, 45f)
        text.alpha = 220
        canvas.drawText(
            "${config.type.emoji} ${config.type.title} • eğ / sürükle",
            w / 2f,
            h * .94f,
            text
        )
    }

    private fun drawProceduralBackground(canvas: Canvas) {
        val flying = config.type.name in setOf("AIRPLANE", "HELICOPTER", "SPACESHIP")

        val top = when (config.type.name) {
            "SPACESHIP" -> Color.rgb(20, 12, 60)
            "AIRPLANE", "HELICOPTER" -> Color.rgb(35, 95, 150)
            "TRACTOR", "BICYCLE" -> Color.rgb(70, 125, 145)
            "TANK" -> Color.rgb(90, 86, 65)
            else -> Color.rgb(28, 7, 55)
        }

        val bottom = when (config.type.name) {
            "TRACTOR", "BICYCLE" -> Color.rgb(18, 42, 28)
            "TANK" -> Color.rgb(31, 34, 24)
            else -> Color.rgb(4, 5, 13)
        }

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h,
            top,
            bottom,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        if (flying) {
            paint.color = Color.argb(170, 230, 240, 255)
            repeat(42) { i ->
                val px = ((i * 83) % w.toInt().coerceAtLeast(1)).toFloat()
                val py = ((i * 137) % (h * .72f).toInt().coerceAtLeast(1)).toFloat()
                val r = if (i % 7 == 0) 3.2f else 1.5f
                canvas.drawCircle(
                    px + roll * (4f + i % 3),
                    py + pitch * (3f + i % 4),
                    r,
                    paint
                )
            }
        } else {
            val horizon = h * .47f

            paint.color = Color.argb(105, 20, 22, 29)
            repeat(10) { i ->
                val bw = w / 10f
                val buildingHeight = h * (.08f + (i % 4) * .035f)
                canvas.drawRect(
                    i * bw,
                    horizon - buildingHeight,
                    i * bw + bw * .72f,
                    horizon,
                    paint
                )
            }

            val road = Path().apply {
                moveTo(w * .41f, horizon)
                lineTo(w * .59f, horizon)
                lineTo(w * .91f, h)
                lineTo(w * .09f, h)
                close()
            }
            paint.color = Color.argb(205, 25, 27, 35)
            canvas.drawPath(road, paint)

            paint.color = Color.argb(185, 255, 255, 255)
            paint.strokeWidth = 5f
            repeat(7) { i ->
                val progress = i / 7f
                val y = horizon + (h - horizon) * progress * progress
                val len = 12f + progress * 52f
                canvas.drawLine(w / 2f, y, w / 2f, y + len, paint)
            }
        }
    }

    private fun drawSpaceship(c: Canvas, x: Float, y: Float, s: Float) {
        val p = Path().apply {
            moveTo(x, y - s)
            lineTo(x + s * .65f, y + s * .42f)
            lineTo(x + s * .22f, y + s * .24f)
            lineTo(x, y + s * .72f)
            lineTo(x - s * .22f, y + s * .24f)
            lineTo(x - s * .65f, y + s * .42f)
            close()
        }
        c.drawPath(p, body)

        body.color = Color.rgb(60, 190, 255)
        c.drawCircle(x - s * .22f, y + s * .48f, s * .12f, body)
        c.drawCircle(x + s * .22f, y + s * .48f, s * .12f, body)
    }

    private fun drawAirplane(c: Canvas, x: Float, y: Float, s: Float) {
        val p = Path().apply {
            moveTo(x, y - s * .8f)
            lineTo(x + s * .22f, y)
            lineTo(x + s, y + s * .25f)
            lineTo(x + s * .18f, y + s * .18f)
            lineTo(x, y + s * .65f)
            lineTo(x - s * .18f, y + s * .18f)
            lineTo(x - s, y + s * .25f)
            lineTo(x - s * .22f, y)
            close()
        }
        c.drawPath(p, body)
    }

    private fun drawHelicopter(c: Canvas, x: Float, y: Float, s: Float) {
        c.drawRoundRect(
            RectF(x - s * .8f, y - s * .25f, x + s * .55f, y + s * .3f),
            s * .22f,
            s * .22f,
            body
        )
        body.strokeWidth = 10f
        c.drawLine(x - s * .05f, y - s * .3f, x + s * .95f, y - s * .3f, body)
        c.drawLine(x + s * .5f, y, x + s * 1.15f, y - s * .2f, body)
    }

    private fun drawBicycle(c: Canvas, x: Float, y: Float, s: Float) {
        body.style = Paint.Style.STROKE
        body.strokeWidth = s * .08f
        c.drawCircle(x - s * .55f, y + s * .15f, s * .35f, body)
        c.drawCircle(x + s * .55f, y + s * .15f, s * .35f, body)
        c.drawLine(x - s * .55f, y + s * .15f, x, y - s * .25f, body)
        c.drawLine(x, y - s * .25f, x + s * .2f, y + s * .15f, body)
        c.drawLine(x + s * .2f, y + s * .15f, x - s * .55f, y + s * .15f, body)
        c.drawLine(x + s * .2f, y + s * .15f, x + s * .55f, y + s * .15f, body)
        body.style = Paint.Style.FILL
    }

    private fun drawTank(c: Canvas, x: Float, y: Float, s: Float) {
        c.drawRoundRect(
            RectF(x - s, y, x + s, y + s * .42f),
            s * .16f,
            s * .16f,
            body
        )
        c.drawOval(
            RectF(x - s * .52f, y - s * .4f, x + s * .3f, y + s * .14f),
            body
        )
        body.strokeWidth = s * .12f
        c.drawLine(x + s * .1f, y - s * .18f, x + s * 1.15f, y - s * .46f, body)
    }

    private fun drawRoadVehicle(c: Canvas, x: Float, y: Float, s: Float) {
        c.drawRoundRect(
            RectF(x - s, y - s * .35f, x + s, y + s * .35f),
            s * .22f,
            s * .22f,
            body
        )

        body.color = Color.argb(220, 20, 25, 35)
        c.drawRoundRect(
            RectF(x - s * .55f, y - s * .62f, x + s * .45f, y - s * .15f),
            s * .16f,
            s * .16f,
            body
        )

        body.color = Color.rgb(18, 18, 22)
        c.drawCircle(x - s * .58f, y + s * .35f, s * .22f, body)
        c.drawCircle(x + s * .58f, y + s * .35f, s * .22f, body)
    }

    override fun release() {
        background?.recycle()
    }
}
