package com.screenmotion.app.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.PI

class MotionController(
    context: Context,
    private val onMotion: (roll: Float, pitch: Float) -> Unit
) : SensorEventListener {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        ?: manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val matrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private var rollFiltered = 0f
    private var pitchFiltered = 0f

    fun start() { sensor?.let { manager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) } }
    fun stop() = manager.unregisterListener(this)

    override fun onSensorChanged(event: SensorEvent) {
        SensorManager.getRotationMatrixFromVector(matrix, event.values)
        SensorManager.getOrientation(matrix, orientation)
        val pitch = orientation[1]
        val roll = orientation[2]
        rollFiltered += 0.12f * (roll - rollFiltered)
        pitchFiltered += 0.12f * (pitch - pitchFiltered)
        onMotion(normalize(rollFiltered, 25f), normalize(pitchFiltered, 20f))
    }

    private fun normalize(radians: Float, maxDegrees: Float): Float {
        val limit = (maxDegrees / 180f * PI).toFloat()
        return (radians / limit).coerceIn(-1f, 1f)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
