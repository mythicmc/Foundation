package org.mythicmc.templateplugin.bungee

import net.md_5.bungee.api.plugin.Plugin

class TemplatePluginKt : Plugin() {
    // lateinit var config: Configuration

    override fun onEnable() {
        // saveDefaultConfig()
        // reloadConfig()
    }

    /* private fun saveDefaultConfig() {
        if (!dataFolder.exists())
            dataFolder.mkdir()

        val file = File(dataFolder, "config.yml")

        if (!file.exists()) {
            try {
                Files.copy(getResourceAsStream("config.yml"), file.toPath())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun reloadConfig() {
        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(File(dataFolder, "config.yml"))
    } */
}
