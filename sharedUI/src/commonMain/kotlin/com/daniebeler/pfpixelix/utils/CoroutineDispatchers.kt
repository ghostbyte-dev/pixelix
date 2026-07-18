package com.daniebeler.pfpixelix.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Dispatcher for blocking IO work. `Dispatchers.io` only exists on the JVM/native targets; the
 * browser is single-threaded and has no filesystem, so wasmJs falls back to `Dispatchers.Default`.
 */
expect val Dispatchers.io: CoroutineDispatcher
