# plugin-template

Quick template to start writing a Paper/Spigot/BungeeCord/Velocity plugin in Kotlin/Java 17

## Quick Start

1. Replace `TemplatePlugin` and `templateplugin` throughout the project, including folder names. Replace `org.mythicmc` if wanted as well.
2. Edit `build.gradle.kts` and resolve the comments starting with `TODO`.
3. If you're targeting only one platform (e.g. Paper, Velocity, etc), move the source code out of the `<platform>` folder and edit `plugin.yml`/`bungee.yml` as needed.
4. Rename `TemplatePluginKt` or `TemplatePluginJava` classes to the name of your plugin.
5. Edit `plugin.yml`, `bungee.yml` and/or `@Plugin` annotation in Velocity and change them as needed (editing, adding, removing properties).
6. Last but not least, remove this Quick Start and change the top heading and description of this README!
