package hikari.core.migration.migrations

import hikari.core.migration.Migration

val migrations: List<Migration>
    get() = listOf(
        SetupBackupCreateMigration(),
        TrustExtensionRepositoryMigration(),
        DefaultExtensionReposMigration(),
        CategoryPreferencesCleanupMigration(),
        InstallationIdMigration(),
    )
