package eu.kanade.tachiyomi.data.backup.create.creators

import eu.kanade.tachiyomi.data.backup.models.BackupExtensionRepos
import eu.kanade.tachiyomi.data.backup.models.backupExtensionReposMapper
import hikari.domain.extensionrepo.interactor.GetExtensionRepo
import tachiyomi.core.common.util.koinGet

class ExtensionRepoBackupCreator(
    private val getExtensionRepos: GetExtensionRepo = koinGet(),
) {

    suspend operator fun invoke(): List<BackupExtensionRepos> {
        return getExtensionRepos.getAll()
            .map(backupExtensionReposMapper)
    }
}
