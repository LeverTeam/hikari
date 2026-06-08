package eu.kanade.tachiyomi.ui.browse.extension

import eu.kanade.domain.extension.interactor.GetExtensionLanguages
import eu.kanade.domain.source.interactor.ToggleLanguage
import eu.kanade.domain.source.service.SourcePreferences
import io.kotest.assertions.nondeterministic.eventually
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tachiyomi.core.common.preference.InMemoryPreferenceStore
import kotlin.time.Duration.Companion.seconds

class ExtensionFilterScreenModelTest {

    private lateinit var preferenceStore: InMemoryPreferenceStore
    private lateinit var preferences: SourcePreferences
    private lateinit var getExtensionLanguages: GetExtensionLanguages
    private lateinit var toggleLanguage: ToggleLanguage
    private lateinit var screenModel: ExtensionFilterScreenModel

    @BeforeEach
    fun setUp() {
        preferenceStore = InMemoryPreferenceStore()
        preferences = SourcePreferences(preferenceStore)
        preferences.enabledLanguages.set(emptySet())
        getExtensionLanguages = mockk()
        toggleLanguage = ToggleLanguage(preferences)
    }

    @Test
    fun initialStateIsSuccessAndDisplaysLanguages() = runBlocking {
        every { getExtensionLanguages.subscribe() } returns flowOf(listOf("en", "ja", "es"))

        screenModel = ExtensionFilterScreenModel(
            preferences = preferences,
            getExtensionLanguages = getExtensionLanguages,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value
            assertInstanceOf(ExtensionFilterState.Success::class.java, state)
            state as ExtensionFilterState.Success
            assertEquals(listOf("en", "ja", "es"), state.languages)
            assertTrue(state.enabledLanguages.isEmpty())
        }
    }

    @Test
    fun toggleChangesLanguageEnablement() = runBlocking {
        every { getExtensionLanguages.subscribe() } returns flowOf(listOf("en", "ja"))

        screenModel = ExtensionFilterScreenModel(
            preferences = preferences,
            getExtensionLanguages = getExtensionLanguages,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            assertInstanceOf(ExtensionFilterState.Success::class.java, screenModel.state.value)
        }

        screenModel.toggle("en")

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertEquals(setOf("en"), state.enabledLanguages)
        }

        screenModel.toggle("en")

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertTrue(state.enabledLanguages.isEmpty())
        }
    }

    @Test
    fun selectAllEnablesAllAvailableLanguages() = runBlocking {
        every { getExtensionLanguages.subscribe() } returns flowOf(listOf("en", "ja", "es"))

        screenModel = ExtensionFilterScreenModel(
            preferences = preferences,
            getExtensionLanguages = getExtensionLanguages,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            assertInstanceOf(ExtensionFilterState.Success::class.java, screenModel.state.value)
        }

        screenModel.selectAll()

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertEquals(setOf("en", "ja", "es"), state.enabledLanguages)
        }
    }

    @Test
    fun selectInverseTogglesEnablementOfAllLanguages() = runBlocking {
        every { getExtensionLanguages.subscribe() } returns flowOf(listOf("en", "ja", "es"))
        preferences.enabledLanguages.set(setOf("en"))

        screenModel = ExtensionFilterScreenModel(
            preferences = preferences,
            getExtensionLanguages = getExtensionLanguages,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertEquals(setOf("en"), state.enabledLanguages)
        }

        screenModel.selectInverse()

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertEquals(setOf("ja", "es"), state.enabledLanguages)
        }
    }

    @Test
    fun resetClearsEnabledLanguages() = runBlocking {
        every { getExtensionLanguages.subscribe() } returns flowOf(listOf("en", "ja"))
        preferences.enabledLanguages.set(setOf("en", "ja"))

        screenModel = ExtensionFilterScreenModel(
            preferences = preferences,
            getExtensionLanguages = getExtensionLanguages,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertEquals(setOf("en", "ja"), state.enabledLanguages)
        }

        screenModel.reset()

        eventually(2.seconds) {
            val state = screenModel.state.value as ExtensionFilterState.Success
            assertTrue("en" in state.enabledLanguages)
        }
    }

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        val mainThreadSurrogate = newSingleThreadContext("UI thread")

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            Dispatchers.setMain(mainThreadSurrogate)
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            Dispatchers.resetMain()
            mainThreadSurrogate.close()
        }
    }
}
