package tachiyomi.presentation.core.util

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import tachiyomi.presentation.core.theme.DefaultSkin
import tachiyomi.presentation.core.theme.Skin

val LocalSkin = staticCompositionLocalOf<Skin> { DefaultSkin }

@Composable
@ReadOnlyComposable
fun currentSkin(): Skin = LocalSkin.current

/**
 * Applies the current [LocalSkin] to the modifier chain.
 * Uses AGSL shaders to skin the component with high-performance materials.
 */
fun Modifier.skin(
    enabled: Boolean = true,
): Modifier = composed {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return@composed Modifier
    }

    val skin = LocalSkin.current
    val color = MaterialTheme.colorScheme.primary

    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                time = it / 1000f
            }
        }
    }

    val runtimeShader = remember(skin) {
        RuntimeShader(skin.shaderCode)
    }

    this.then(
        Modifier.graphicsLayer {
            skin.updateUniforms(runtimeShader, time, color)
            renderEffect = RenderEffect.createRuntimeShaderEffect(
                runtimeShader,
                "content",
            ).asComposeRenderEffect()
        },
    )
}

/**
 * Provides a [Skin] to the local composition.
 */
@Composable
fun ProvideSkin(skin: Skin, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSkin provides skin, content = content)
}
