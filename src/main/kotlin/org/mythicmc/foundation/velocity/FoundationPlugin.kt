package org.mythicmc.foundation.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.mythicmc.foundation.BuildMetadata
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "foundation",
    name = "Foundation",
    authors = ["retrixe"],
    version = BuildMetadata.VERSION,
    description = BuildMetadata.DESCRIPTION,
    url = "https://github.com/mythicmc/Foundation",
    dependencies = []
)
class FoundationPlugin @Inject constructor(
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path
) {
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {}
}
