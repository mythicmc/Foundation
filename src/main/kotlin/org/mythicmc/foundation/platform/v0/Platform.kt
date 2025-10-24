package org.mythicmc.foundation.platform.v0

import com.velocitypowered.api.proxy.ProxyServer
import org.bukkit.plugin.java.JavaPlugin
import org.mythicmc.foundation.platform.v0.internal.BukkitPlatform
import org.mythicmc.foundation.platform.v0.internal.VelocityPlatform
import org.slf4j.Logger
import java.io.InputStream
import java.nio.file.Path

/**
 * An interface that works across various server software to provide common functionality for
 * plugins.
 *
 * This helps Foundation work correctly across Velocity and Paper while sharing a single codebase.
 *
 * You can get an instance of [Platform] through the functions in [Platform.Factory].
 */
interface Platform {
    /**
     * Provides functions to get an instance of [Platform].
     */
    companion object Factory {
        /**
         * Retrieve a [Platform] instance for a Bukkit plugin.
         *
         * @param plugin the main [JavaPlugin] class of the Bukkit plugin
         * @return a new [Platform] for the given Bukkit plugin
         */
        @JvmStatic fun bukkit(plugin: JavaPlugin): Platform =
            BukkitPlatform(plugin)

        /**
         * Retrieve a [Platform] instance for a Velocity plugin.
         *
         * @param plugin the instance of the Velocity plugin
         * @param server the [ProxyServer] instance received by the Velocity plugin
         * @param logger the [Logger] instance received by the Velocity plugin
         * @param dataDirectory the [Path] to the Velocity plugin's data directory
         * @return a new [Platform] for the given Velocity plugin
         */
        @JvmStatic fun velocity(
            plugin: Any,
            server: ProxyServer,
            logger: Logger,
            dataDirectory: Path
        ): Platform =
            VelocityPlatform(plugin, server, logger, dataDirectory)
    }

    /**
     * Get the contents of a resource file in the plugin JAR.
     *
     * @param name the name of the resource file
     * @return an [InputStream] with the file contents, or `null` if the resource doesn't exist
     */
    fun getResource(name: String): InputStream?

    /**
     * Get the [Path] to the plugin's data directory, for storing configuration and cache files.
     *
     * @return the [Path] to the plugin's data directory
     */
    val dataDirectory: Path

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    fun info(msg: String)

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    fun warn(msg: String)
}
