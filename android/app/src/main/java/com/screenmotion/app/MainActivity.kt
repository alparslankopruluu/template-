package com.screenmotion.app

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.screenmotion.app.data.ConfigRepository
import com.screenmotion.app.model.ThemeType
import com.screenmotion.app.motion.MotionController
import com.screenmotion.app.wallpaper.InteractiveWallpaperService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6E6CFF),
                    secondary = Color(0xFF30D6FF),
                    background = Color(0xFF050914),
                    surface = Color(0xFF0D1424)
                )
            ) {
                ScreenMotionApp()
            }
        }
    }
}

@Composable
private fun ScreenMotionApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("screen_motion_ui", MODE_PRIVATE) }
    var onboardingDone by remember { mutableStateOf(prefs.getBoolean("onboarding_done", false)) }

    if (!onboardingDone) {
        OnboardingScreen {
            prefs.edit().putBoolean("onboarding_done", true).apply()
            onboardingDone = true
        }
    } else {
        ThemePickerScreen()
    }
}

@Composable
private fun OnboardingScreen(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val types = listOf(ThemeType.CAR, ThemeType.AQUARIUM, ThemeType.SPACESHIP)
    val titles = listOf(
        "Telefonunu eğ. Sahne hareket etsin.",
        "Parmağınla dokun. Dünya sana cevap versin.",
        "Her ekrana yaşayan bir tema."
    )
    val subtitles = listOf(
        "Gyroscope ile araç, kamera ve arka plan gerçek zamanlı tepki verir.",
        "Balıkları yönlendir, yıldızları sürükle, araçları şerit değiştir.",
        "Araçlar, uzay, akvaryum ve doğa temaları tek uygulamada."
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF040714), Color(0xFF10183A), Color(0xFF050914))
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("ScreenMotion", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MotionDemo(
                type = types[page],
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
            )
            Spacer(Modifier.height(28.dp))
            Text(
                titles[page],
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(10.dp))
            Text(subtitles[page], color = Color(0xFFB9C2DD), fontSize = 16.sp)
        }

        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { i ->
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(if (i == page) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (i == page) Color(0xFF6E6CFF) else Color(0xFF39415A))
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { if (page < 2) page++ else onFinish() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (page < 2) "Devam" else "Temaları Keşfet")
            }
        }
    }
}

@Composable
private fun MotionDemo(type: ThemeType, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    val animatedRoll by animateFloatAsState(roll, label = "roll")

    val controller = remember {
        MotionController(context) { r, p ->
            roll = r
            pitch = p
        }
    }
    DisposableEffect(Unit) {
        controller.start()
        onDispose { controller.stop() }
    }

    Box(
        modifier
            .clip(RoundedCornerShape(36.dp))
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF25377A), Color(0xFF101A42), Color(0xFF030610))
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { dragX = 0f; dragY = 0f },
                    onDragCancel = { dragX = 0f; dragY = 0f }
                ) { _, delta ->
                    dragX += delta.x
                    dragY += delta.y
                }
            }
    ) {
        repeat(26) { i ->
            Box(
                Modifier
                    .offset(x = ((i * 47) % 320).dp, y = ((i * 83) % 350).dp)
                    .size(if (i % 4 == 0) 3.dp else 2.dp)
                    .background(Color.White.copy(alpha = .35f), RoundedCornerShape(99.dp))
            )
        }

        Text(
            type.emoji,
            fontSize = if (type == ThemeType.AQUARIUM) 72.sp else 86.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = ((animatedRoll * 90f) + dragX / 4f).dp,
                    y = ((pitch * 35f) + dragY / 6f).dp
                )
        )

        Surface(
            color = Color.Black.copy(alpha = .42f),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp)
        ) {
            Text(
                "Telefonu eğ veya parmağınla sürükle",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ThemePickerScreen() {
    val context = LocalContext.current
    val repository = remember { ConfigRepository(context) }
    var config by remember { mutableStateOf(repository.load()) }

    Scaffold(containerColor = Color(0xFF050914)) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("ScreenMotion", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("Yaşayan kilit ve ana ekranlar", color = Color(0xFF96A2C6))

            Spacer(Modifier.height(18.dp))
            MotionDemo(config.type, Modifier.fillMaxWidth().height(290.dp))

            Spacer(Modifier.height(18.dp))
            Text("Tema seç", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ThemeType.entries) { theme ->
                    val selected = theme == config.type
                    Card(
                        onClick = { config = config.copy(type = theme) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFF252C69) else Color(0xFF0E1627)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(theme.emoji, fontSize = 34.sp)
                            Text(theme.title, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Gyroscope", color = Color.White, modifier = Modifier.weight(1f))
                Switch(
                    checked = config.gyroEnabled,
                    onCheckedChange = { config = config.copy(gyroEnabled = it) }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dokunma / sürükleme", color = Color.White, modifier = Modifier.weight(1f))
                Switch(
                    checked = config.touchEnabled,
                    onCheckedChange = { config = config.copy(touchEnabled = it) }
                )
            }
            Text("Hareket yoğunluğu", color = Color(0xFFB9C2DD))
            Slider(
                value = config.parallaxStrength,
                onValueChange = { config = config.copy(parallaxStrength = it) },
                valueRange = .4f..1.6f
            )

            Button(
                onClick = {
                    repository.save(config)
                    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            ComponentName(context, InteractiveWallpaperService::class.java)
                        )
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Canlı Duvar Kağıdını Uygula")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
