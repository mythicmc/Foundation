package org.mythicmc.foundation.platform.v0.internal

import com.velocitypowered.api.proxy.ProxyServer
import org.mythicmc.foundation.platform.v0.Platform
import org.slf4j.Logger
import java.io.InputStream
import java.nio.file.Path

internal class VelocityPlatform(
    val plugin: Any,
    val server: ProxyServer,
    val logger: Logger,
    override val dataDirectory: Path
) : Platform {
    override fun getResource(name: String): InputStream? =
        plugin.javaClass.classLoader.getResourceAsStream(name)

    override fun info(msg: String) =
        logger.info(msg)

    override fun warn(msg: String) =
        logger.warn(msg)
}
