package tachiyomi.presentation.core.util

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransitionContent
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance
import cafe.adriel.voyager.core.screen.Screen as VoyagerScreen

/**
 * For invoking back press to the parent activity
 */
val LocalBackPress: ProvidableCompositionLocal<(() -> Unit)?> = staticCompositionLocalOf { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedContentScope?> { null }

@Composable
fun DefaultNavigatorScreenTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val slideDistance = rememberSlideDistance()
    ScreenTransition(
        navigator = navigator,
        transition = {
            materialSharedAxisX(
                forward = navigator.lastEvent != StackEvent.Pop,
                slideDistance = slideDistance,
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.mangaSharedElement(
    tag: String,
    mangaId: Long?,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
    return if (sharedTransitionScope != null && animatedVisibilityScope != null && mangaId != null) {
        with(sharedTransitionScope) {
            this@mangaSharedElement.sharedElement(
                rememberSharedContentState(key = "manga-$tag-$mangaId"),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    } else {
        this
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScreenTransition(
    navigator: Navigator,
    transition: AnimatedContentTransitionScope<VoyagerScreen>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: ScreenTransitionContent = { it.Content() },
) {
    SharedTransitionLayout {
        AnimatedContent(
            targetState = navigator.lastItem,
            transitionSpec = {
                val transition = transition()
                val (enter, exit) = transition.targetContentEnter to transition.initialContentExit

                val scaleEnter = androidx.compose.animation.scaleIn(
                    initialScale = 0.96f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                )
                val scaleExit = androidx.compose.animation.scaleOut(
                    targetScale = 0.96f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                )

                (enter + scaleEnter) togetherWith (exit + scaleExit)
            },
            modifier = modifier,
            label = "transition",
        ) { screen ->
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                LocalNavAnimatedVisibilityScope provides this,
            ) {
                navigator.saveableState("transition", screen) {
                    content(screen)
                }
            }
        }
    }

    BackHandler(enabled = navigator.canPop, onBack = navigator::pop)
}

abstract class Screen : VoyagerScreen {
    open fun onProvideAssistUrl(): String? = null
}

interface AssistContentScreen {
    fun onProvideAssistUrl(): String?
}

interface Tab : cafe.adriel.voyager.navigator.tab.Tab {
    suspend fun onReselect(navigator: Navigator) {}
}

@Composable
fun saveableState(
    key: String,
    tab: VoyagerScreen,
    content: @Composable () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    navigator.saveableState(key, tab, content)
}
