package hikari.core.migration.migrations

import hikari.core.migration.Migration
import hikari.core.migration.MigrationContext
import hikari.domain.extensionrepo.interactor.CreateExtensionRepo
import hikari.domain.extensionrepo.repository.ExtensionRepoRepository
import tachiyomi.core.common.util.lang.withIOContext

class DefaultExtensionReposMigration : Migration {
    override val version: Float = Migration.ALWAYS

    override suspend fun invoke(migrationContext: MigrationContext): Boolean = withIOContext {
        val extensionRepositoryRepository =
            migrationContext.get<ExtensionRepoRepository>() ?: return@withIOContext false
        val createExtensionRepo = migrationContext.get<CreateExtensionRepo>() ?: return@withIOContext false

        val defaultRepo = "https://raw.githubusercontent.com/LeverTeam/hikari-extensions/repo/index.min.json"
        val baseUrl = defaultRepo.removeSuffix("/index.min.json")

        if (extensionRepositoryRepository.getRepo(baseUrl) == null) {
            createExtensionRepo.await(defaultRepo)
        }

        true
    }
}
