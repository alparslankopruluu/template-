package com.screenmotion.app.render

import android.content.Context
import com.screenmotion.app.model.ThemeConfig
import com.screenmotion.app.model.ThemeType

object RendererFactory {
    fun create(context: Context, config: ThemeConfig): SceneRenderer = when(config.type) {
        ThemeType.SPACE -> SpaceSceneRenderer(context, config)
        ThemeType.AQUARIUM -> AquariumSceneRenderer(context, config)
        ThemeType.NATURE -> NatureSceneRenderer(context, config)
        else -> VehicleSceneRenderer(context, config)
    }
}
