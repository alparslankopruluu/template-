package com.screenmotion.app.render

import android.content.Context
import android.graphics.*
import com.screenmotion.app.model.ThemeConfig
import kotlin.random.Random

private data class Star(var x: Float, var y: Float, val z: Float, val r: Float)

class SpaceSceneRenderer(context: Context, private val config: ThemeConfig) : SceneRenderer {
    private val background = loadAssetBitmap(context, "wallpapers/space.jpg")
    private val stars = MutableList((config.particleAmount * 3).coerceAtLeast(80)) { Star(Random.nextFloat(), Random.nextFloat(), .25f + Random.nextFloat()*1.75f, 1f+Random.nextFloat()*2.2f) }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var w=0f; private var h=0f; private var roll=0f; private var pitch=0f; private var drag=0f
    override fun onSizeChanged(width:Int,height:Int){w=width.toFloat();h=height.toFloat()}
    override fun onMotion(roll:Float,pitch:Float){if(config.gyroEnabled){this.roll=roll;this.pitch=pitch}}
    override fun onTouch(x:Float,y:Float,down:Boolean){drag=if(config.touchEnabled&&down) (x-w/2f)/(w/2f) else 0f}
    override fun update(dt:Float){stars.forEach{it.y += dt*.045f*it.z*config.speed; it.x += dt*(roll+drag*.7f)*.018f*it.z; if(it.y>1.05f)it.y=-.05f; if(it.x>1.05f)it.x=-.05f; if(it.x<-.05f)it.x=1.05f}}
    override fun draw(canvas:Canvas){
        canvas.drawColor(Color.BLACK); canvas.drawCenterCrop(background,-roll*45f,-pitch*28f,1.12f)
        stars.forEach{ s -> val x=s.x*w+roll*26f*s.z+drag*18f*s.z; val y=s.y*h+pitch*15f*s.z; paint.color=Color.argb((100+s.z*80).toInt().coerceIn(80,255),225,235,255); canvas.drawCircle(x,y,s.r*s.z,paint)}
        paint.color=Color.argb(190,110,190,255); canvas.drawCircle(w*.5f+roll*w*.2f,h*.73f+pitch*25f,12f,paint)
    }
    override fun release(){background?.recycle()}
}
