package com.screenmotion.app.model

data class ThemeConfig(
    val type: ThemeType = ThemeType.SPACE,
    val gyroEnabled: Boolean = true,
    val touchEnabled: Boolean = true,
    val speed: Float = 1f,
    val particleAmount: Int = 48,
    val parallaxStrength: Float = 1f,
    val fps: Int = 30
)
