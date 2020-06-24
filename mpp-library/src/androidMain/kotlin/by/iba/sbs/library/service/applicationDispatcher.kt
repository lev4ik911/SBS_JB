package by.iba.sbs.library.service

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual val applicationDispatcher: CoroutineContext = Dispatchers.IO