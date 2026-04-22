package eu.kanade.tachiyomi.ui.base.delegate

import android.app.Activity
import eu.kanade.tachiyomi.R
import org.koin.core.component.KoinComponent
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.ui.UiPreferences
import tachiyomi.domain.ui.model.AppTheme

interface ThemingDelegate {
    fun applyAppTheme(activity: Activity)

    companion object {
        fun getThemeResIds(appTheme: AppTheme, isAmoled: Boolean): List<Int> {
            return buildList(2) {
                add(themeResources.getOrDefault(appTheme, R.style.Theme_Tachiyomi))
                if (isAmoled) add(R.style.ThemeOverlay_Tachiyomi_Amoled)
            }
        }
    }
}

class ThemingDelegateImpl : ThemingDelegate, KoinComponent {
    override fun applyAppTheme(activity: Activity) {
        val uiPreferences = koinGet<UiPreferences>()
        ThemingDelegate.getThemeResIds(uiPreferences.appTheme.get(), uiPreferences.themeDarkAmoled.get())
            .forEach(activity::setTheme)
    }
}

private val themeResources: Map<AppTheme, Int> = mapOf(
    AppTheme.MONET to R.style.Theme_Tachiyomi_Monet,
)
