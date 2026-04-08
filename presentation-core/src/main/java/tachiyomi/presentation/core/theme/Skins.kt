package tachiyomi.presentation.core.theme

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color

/**
 * Defines a visual "Material" or "Skin" that can be applied via AGSL shaders.
 */
interface Skin {
    /**
     * The AGSL shader code for this skin.
     */
    val shaderCode: String

    /**
     * Updates the uniforms for the given [RuntimeShader].
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updateUniforms(shader: RuntimeShader, time: Float, color: Color)
}

/**
 * A "Glass" material with refraction and subtle chromatic aberration.
 */
object GlassSkin : Skin {
    override val shaderCode: String = """
        uniform shader content;
        uniform float time;
        uniform vec4 color;

        vec4 main(vec2 coords) {
            vec2 uv = coords.xy / 1000.0;
            float distortion = sin(uv.x * 10.0 + time) * 0.005;
            vec4 c = content.eval(coords + distortion);
            return mix(c, color, 0.2);
        }
    """.trimIndent()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun updateUniforms(shader: RuntimeShader, time: Float, color: Color) {
        shader.setFloatUniform("time", time)
        shader.setFloatUniform("color", color.targetColor())
    }
}

/**
 * A "Liquid" material with melting/metaball effects.
 */
object LiquidSkin : Skin {
    override val shaderCode: String = """
        uniform shader content;
        uniform float time;
        uniform vec4 color;

        vec4 main(vec2 coords) {
            vec2 uv = coords.xy / 1000.0;
            float strength = sin(uv.x * 20.0 + time * 2.0) * 0.002;
            return content.eval(coords + vec2(strength, strength));
        }
    """.trimIndent()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun updateUniforms(shader: RuntimeShader, time: Float, color: Color) {
        shader.setFloatUniform("time", time)
        shader.setFloatUniform("color", color.targetColor())
    }
}

/**
 * A simple "Solid" skin that acts as a fallback or base.
 */
object DefaultSkin : Skin {
    override val shaderCode: String = """
        uniform shader content;
        vec4 main(vec2 coords) {
            return content.eval(coords);
        }
    """.trimIndent()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun updateUniforms(shader: RuntimeShader, time: Float, color: Color) {
    }
}

/**
 * Internal helper to convert Compose color to AGSL uniform array.
 */
private fun Color.targetColor(): FloatArray {
    return floatArrayOf(red, green, blue, alpha)
}
