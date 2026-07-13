package com.daniebeler.pfpixelix.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Dispatcher for blocking IO work. `Dispatchers.io` only exists on the JVM/native targets; the
 * browser is single-threaded and has no filesystem, so wasmJs falls back to `Dispatchers.Default`.
 */
val Dispatchers.io: CoroutineDispatcher get() = Dispatchers.IO
