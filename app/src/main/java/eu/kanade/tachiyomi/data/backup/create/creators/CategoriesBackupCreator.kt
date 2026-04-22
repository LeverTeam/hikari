package eu.kanade.tachiyomi.data.backup.create.creators

import eu.kanade.tachiyomi.data.backup.models.BackupCategory
import eu.kanade.tachiyomi.data.backup.models.backupCategoryMapper
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.category.model.Category

class CategoriesBackupCreator(
    private val getCategories: GetCategories = koinGet(),
) {

    suspend operator fun invoke(): List<BackupCategory> {
        return getCategories.await()
            .filterNot(Category::isSystemCategory)
            .map(backupCategoryMapper)
    }
}
