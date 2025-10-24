package org.mythicmc.foundation.paper

import org.bukkit.plugin.java.JavaPlugin
import org.mythicmc.foundation.platform.v0.Platform

class FoundationPlugin : JavaPlugin() {
    override fun onEnable() {
        org.mythicmc.foundation.platform.v0.load(Platform.bukkit(this))
        org.mythicmc.foundation.rosetta.v1.load()
    }
}
