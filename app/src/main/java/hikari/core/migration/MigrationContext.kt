package hikari.core.migration

import org.koin.core.component.KoinComponent

class MigrationContext(val dryrun: Boolean) : KoinComponent {

    inline fun <reified T : Any> get(): T? {
        return getKoin().getOrNull<T>()
    }
}
