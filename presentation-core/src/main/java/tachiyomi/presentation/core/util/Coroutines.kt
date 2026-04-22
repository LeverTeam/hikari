package tachiyomi.presentation.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
