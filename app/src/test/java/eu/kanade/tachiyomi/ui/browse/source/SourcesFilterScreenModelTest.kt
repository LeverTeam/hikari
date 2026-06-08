package eu.kanade.tachiyomi.ui.browse.source

import eu.kanade.domain.source.interactor.GetLanguagesWithSources
import eu.kanade.domain.source.interactor.ToggleLanguage
import eu.kanade.domain.source.interactor.ToggleSource
import eu.kanade.domain.source.service.SourcePreferences
import io.kotest.assertions.nondeterministic.eventually
import io.mockk.every
import io.mockk.mockk
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
import tachiyomi.domain.source.model.Source
import java.util.TreeMap
import kotlin.time.Duration.Companion.seconds

class SourcesFilterScreenModelTest {

    private lateinit var preferenceStore: InMemoryPreferenceStore
    private lateinit var preferences: SourcePreferences
    private lateinit var getLanguagesWithSources: GetLanguagesWithSources
    private lateinit var toggleSource: ToggleSource
    private lateinit var toggleLanguage: ToggleLanguage
    private lateinit var screenModel: SourcesFilterScreenModel

    private val testSource1 = Source(1L, "en", "Source 1", supportsLatest = true, isStub = false)
    private val testSource2 = Source(2L, "ja", "Source 2", supportsLatest = true, isStub = false)

    @BeforeEach
    fun setUp() {
        preferenceStore = InMemoryPreferenceStore()
        preferences = SourcePreferences(preferenceStore)
        preferences.enabledLanguages.set(emptySet())
        getLanguagesWithSources = mockk()
        toggleSource = ToggleSource(preferences)
        toggleLanguage = ToggleLanguage(preferences)
    }

    @Test
    fun initialStateIsSuccessAndDisplaysLanguagesAndSources() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
            put("ja", listOf(testSource2))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value
            assertInstanceOf(SourcesFilterScreenModel.State.Success::class.java, state)
            state as SourcesFilterScreenModel.State.Success
            assertEquals(items, state.items)
            assertTrue(state.enabledLanguages.isEmpty())
            assertTrue(state.disabledSources.isEmpty())
        }
    }

    @Test
    fun toggleSourceChangesSourceDisablement() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            assertInstanceOf(SourcesFilterScreenModel.State.Success::class.java, screenModel.state.value)
        }

        screenModel.toggleSource(testSource1)

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("1"), state.disabledSources)
        }

        screenModel.toggleSource(testSource1)

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertTrue(state.disabledSources.isEmpty())
        }
    }

    @Test
    fun toggleLanguageChangesLanguageEnablement() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            assertInstanceOf(SourcesFilterScreenModel.State.Success::class.java, screenModel.state.value)
        }

        screenModel.toggleLanguage("en")

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("en"), state.enabledLanguages)
        }
    }

    @Test
    fun selectAllEnablesAllAvailableLanguages() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
            put("ja", listOf(testSource2))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            assertInstanceOf(SourcesFilterScreenModel.State.Success::class.java, screenModel.state.value)
        }

        screenModel.selectAll()

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("en", "ja"), state.enabledLanguages)
        }
    }

    @Test
    fun selectInverseTogglesEnablementOfAllLanguages() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
            put("ja", listOf(testSource2))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)
        preferences.enabledLanguages.set(setOf("en"))

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("en"), state.enabledLanguages)
        }

        screenModel.selectInverse()

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("ja"), state.enabledLanguages)
        }
    }

    @Test
    fun resetClearsEnabledLanguagesAndDisabledSources() = runBlocking {
        val items = TreeMap<String, List<Source>>().apply {
            put("en", listOf(testSource1))
            put("ja", listOf(testSource2))
        }
        every { getLanguagesWithSources.subscribe() } returns flowOf(items)
        preferences.enabledLanguages.set(setOf("en", "ja"))
        preferences.disabledSources.set(setOf("1"))

        screenModel = SourcesFilterScreenModel(
            preferences = preferences,
            getLanguagesWithSources = getLanguagesWithSources,
            toggleSource = toggleSource,
            toggleLanguage = toggleLanguage,
        )

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertEquals(setOf("en", "ja"), state.enabledLanguages)
            assertEquals(setOf("1"), state.disabledSources)
        }

        screenModel.reset()

        eventually(2.seconds) {
            val state = screenModel.state.value as SourcesFilterScreenModel.State.Success
            assertTrue(state.disabledSources.isEmpty())
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
