package com.screenmotion.app.render

import android.content.Context
import android.graphics.*
import com.screenmotion.app.model.ThemeConfig
import kotlin.random.Random

class NatureSceneRenderer(context:Context, private val config:ThemeConfig):SceneRenderer{
    private val bg=loadAssetBitmap(context,"wallpapers/nature.jpg");private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
    private val motes=MutableList(config.particleAmount){floatArrayOf(Random.nextFloat(),Random.nextFloat(),.5f+Random.nextFloat())}
    private var w=0f;private var h=0f;private var roll=0f;private var pitch=0f;private var drag=0f
    override fun onSizeChanged(width:Int,height:Int){w=width.toFloat();h=height.toFloat()}
    override fun onMotion(roll:Float,pitch:Float){if(config.gyroEnabled){this.roll=roll;this.pitch=pitch}}
    override fun onTouch(x:Float,y:Float,down:Boolean){drag=if(config.touchEnabled&&down)(x-w/2)/(w/2) else 0f}
    override fun update(dt:Float){motes.forEach{it[1]-=dt*.01f*it[2];it[0]+=dt*(roll+drag)*.006f;if(it[1]<0)it[1]=1f;if(it[0]<0)it[0]=1f;if(it[0]>1)it[0]=0f}}
    override fun draw(c:Canvas){c.drawColor(Color.rgb(10,30,25));c.drawCenterCrop(bg,-(roll+drag*.5f)*40f,-pitch*26f,1.1f);paint.color=Color.argb(100,255,240,190);motes.forEach{c.drawCircle(it[0]*w+roll*12f,it[1]*h,it[2]*2.2f,paint)}}
    override fun release(){bg?.recycle()}
}
