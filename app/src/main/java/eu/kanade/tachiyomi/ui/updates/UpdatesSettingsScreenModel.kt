package eu.kanade.tachiyomi.ui.updates

import cafe.adriel.voyager.core.model.ScreenModel
import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.preference.TriState
import tachiyomi.core.common.preference.getAndSet
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.updates.service.UpdatesPreferences

class UpdatesSettingsScreenModel(
    val updatesPreferences: UpdatesPreferences = koinGet(),
) : ScreenModel {

    fun toggleFilter(preference: (UpdatesPreferences) -> Preference<TriState>) {
        preference(updatesPreferences).getAndSet {
            it.next()
        }
    }
}
