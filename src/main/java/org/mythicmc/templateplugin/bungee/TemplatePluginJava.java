package org.mythicmc.templateplugin.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class TemplatePluginJava extends Plugin {
    // private Configuration config;

    @Override
    public void onEnable() {
        // saveDefaultConfig();
        // reloadConfig();
    }

    /* private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadConfig() {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    public Configuration getConfig() {
        return config;
    } */
}
