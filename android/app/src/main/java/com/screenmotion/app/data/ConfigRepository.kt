package com.screenmotion.app.data

import android.content.Context
import com.screenmotion.app.model.ThemeConfig
import com.screenmotion.app.model.ThemeType

class ConfigRepository(context: Context) {
    private val prefs = context.getSharedPreferences("screen_motion", Context.MODE_PRIVATE)

    fun save(config: ThemeConfig) {
        prefs.edit()
            .putString("type", config.type.name)
            .putBoolean("gyro", config.gyroEnabled)
            .putBoolean("touch", config.touchEnabled)
            .putFloat("speed", config.speed)
            .putInt("particles", config.particleAmount)
            .putFloat("parallax", config.parallaxStrength)
            .putInt("fps", config.fps)
            .apply()
    }

    fun load(): ThemeConfig {
        val type = runCatching {
            ThemeType.valueOf(prefs.getString("type", ThemeType.SPACE.name) ?: ThemeType.SPACE.name)
        }.getOrDefault(ThemeType.SPACE)
        return ThemeConfig(
            type = type,
            gyroEnabled = prefs.getBoolean("gyro", true),
            touchEnabled = prefs.getBoolean("touch", true),
            speed = prefs.getFloat("speed", 1f),
            particleAmount = prefs.getInt("particles", 48),
            parallaxStrength = prefs.getFloat("parallax", 1f),
            fps = prefs.getInt("fps", 30)
        )
    }
}
