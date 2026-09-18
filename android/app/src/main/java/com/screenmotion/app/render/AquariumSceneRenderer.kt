package com.screenmotion.app.render

import android.content.Context
import android.graphics.*
import com.screenmotion.app.model.ThemeConfig
import kotlin.math.*
import kotlin.random.Random

private data class Fish(var x:Float,var y:Float,var vx:Float,var vy:Float,val size:Float,val color:Int)
private data class Bubble(var x:Float,var y:Float,val r:Float,val speed:Float)

class AquariumSceneRenderer(context: Context, private val config: ThemeConfig) : SceneRenderer {
    private val background=loadAssetBitmap(context,"wallpapers/aquarium.jpg")
    private val fish=MutableList(7){Fish(Random.nextFloat(),.2f+Random.nextFloat()*.65f,(Random.nextFloat()-.5f)*.16f,(Random.nextFloat()-.5f)*.05f,.045f+Random.nextFloat()*.035f,Color.HSVToColor(floatArrayOf(Random.nextFloat()*360f,.75f,1f)))}
    private val bubbles=MutableList(config.particleAmount.coerceAtLeast(24)){Bubble(Random.nextFloat(),Random.nextFloat(),2f+Random.nextFloat()*7f,.035f+Random.nextFloat()*.06f)}
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG); private var w=0f;private var h=0f;private var roll=0f;private var pitch=0f; private var touchX=.5f;private var touchY=.5f;private var down=false
    override fun onSizeChanged(width:Int,height:Int){w=width.toFloat();h=height.toFloat()}
    override fun onMotion(roll:Float,pitch:Float){if(config.gyroEnabled){this.roll=roll;this.pitch=pitch}}
    override fun onTouch(x:Float,y:Float,down:Boolean){if(config.touchEnabled){touchX=(x/w).coerceIn(0f,1f);touchY=(y/h).coerceIn(0f,1f);this.down=down}}
    override fun update(dt:Float){
        fish.forEach{f->
            f.vx += roll*.025f*dt; f.vy += pitch*.012f*dt
            if(down){val dx=touchX-f.x;val dy=touchY-f.y;val d=sqrt(dx*dx+dy*dy).coerceAtLeast(.03f);f.vx += dx/d*.035f*dt;f.vy += dy/d*.025f*dt}
            f.x += f.vx*dt*config.speed; f.y += f.vy*dt*config.speed; f.vx*=.997f;f.vy*=.997f
            if(f.x<-.1f)f.x=1.1f;if(f.x>1.1f)f.x=-.1f;if(f.y<.08f){f.y=.08f;f.vy=abs(f.vy)};if(f.y>.92f){f.y=.92f;f.vy=-abs(f.vy)}
        }
        bubbles.forEach{it.y-=it.speed*dt*config.speed;if(it.y<-.04f){it.y=1.04f;it.x=Random.nextFloat()}}
    }
    override fun draw(canvas:Canvas){
        canvas.drawColor(Color.rgb(0,25,55));canvas.drawCenterCrop(background,-roll*28f,-pitch*18f,1.08f)
        bubbles.forEach{b->paint.style=Paint.Style.STROKE;paint.strokeWidth=2f;paint.color=Color.argb(130,230,250,255);canvas.drawCircle(b.x*w+roll*10f,b.y*h,b.r,paint)}
        paint.style=Paint.Style.FILL
        fish.forEach{f->drawFish(canvas,f)}
        if(down){paint.style=Paint.Style.STROKE;paint.strokeWidth=4f;paint.color=Color.argb(130,255,255,255);canvas.drawCircle(touchX*w,touchY*h,42f,paint);paint.style=Paint.Style.FILL}
    }
    private fun drawFish(c:Canvas,f:Fish){val x=f.x*w;val y=f.y*h;val s=f.size*w.coerceAtMost(h*.45f);c.save();c.translate(x,y);c.scale(if(f.vx>=0)1f else -1f,1f);paint.color=f.color;c.drawOval(RectF(-s,-s*.45f,s,s*.45f),paint);val p=Path().apply{moveTo(-s*.85f,0f);lineTo(-s*1.55f,-s*.55f);lineTo(-s*1.55f,s*.55f);close()};c.drawPath(p,paint);paint.color=Color.WHITE;c.drawCircle(s*.55f,-s*.1f,s*.09f,paint);paint.color=Color.BLACK;c.drawCircle(s*.58f,-s*.1f,s*.04f,paint);c.restore()}
    override fun release(){background?.recycle()}
}
