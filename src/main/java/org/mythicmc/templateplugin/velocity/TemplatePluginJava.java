package org.mythicmc.templateplugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import java.nio.file.Path;

@Plugin(
        id = "templateplugin",
        name = "TemplatePlugin",
        authors = {}, // TODO: Set me!
        version = BuildMetadata.VERSION,
        description = BuildMetadata.DESCRIPTION,
        url = "", // TODO: Set me!
        dependencies = {}
)
public class TemplatePluginJava {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public TemplatePluginJava(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {}
}
