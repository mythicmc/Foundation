package org.mythicmc.foundation.platform.v0.internal

import org.bukkit.plugin.java.JavaPlugin
import org.mythicmc.foundation.platform.v0.Platform
import java.io.InputStream
import java.nio.file.Path

internal class BukkitPlatform(val plugin: JavaPlugin) : Platform {
    override fun getResource(name: String): InputStream? =
        plugin.getResource(name)

    override val dataDirectory: Path =
        plugin.dataFolder.toPath()

    override fun info(msg: String) =
        plugin.logger.info(msg)
}
