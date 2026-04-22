package eu.kanade.tachiyomi.util.system

import tachiyomi.domain.source.service.LanguageComparator

class LanguageComparatorImpl : LanguageComparator {
    override fun compare(a: String, b: String): Int {
        return LocaleHelper.comparator(a, b)
    }
}
