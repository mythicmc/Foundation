package org.mythicmc.foundation.platform.v0.internal

import org.mythicmc.foundation.platform.v0.Platform

/**
 * The [Platform] for the Foundation plugin. This platform is only intended for internal usage!
 *
 * Examples of valid usage include:
 * - Loading Foundation-specific configuration.
 * - Loading configuration global to all plugins dependent on Foundation.
 * - Errors internal to Foundation, unrelated to any consumer of the Foundation API.
 */
internal lateinit var FoundationPlatform: Platform
