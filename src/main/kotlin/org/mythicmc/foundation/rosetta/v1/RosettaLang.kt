package org.mythicmc.foundation.rosetta.v1

import net.kyori.adventure.pointer.Pointered
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.mythicmc.foundation.platform.v0.Platform
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

/**
 * An extensible class to handle `lang.yml` files, built using MiniMessage for formatting.
 *
 * This class works well with the rest of the `rosetta-v1` module. The default MiniMessage instance
 * enables the `<prefix>` tag (resolved using [Rosetta.PrefixTagResolver]) when parsing Lang
 * strings.
 *
 * Consuming plugins should create a singleton storing an instance of this class, to make use of it.
 *
 * ```
 * // Example in the main plugin class:
 * val LANG = RosettaLang(Platform.bukkit(this)) // or Platform.velocity
 *
 * // In `onEnable` / `onProxyInitialization` / etc:
 * LANG.reloadLangFile()
 * ```
 */
class RosettaLang(private val platform: Platform) {
    private val dataFolder = platform.dataDirectory
    private val defaultLangFile = platform.getResource("lang.yml")!!

    private val langMap = HashMap<String, String>()
    private val yaml = Yaml()
    private val miniMessage = MiniMessage.builder()
        .tags(TagResolver.standard())
        .editTags { it.resolver(Rosetta.PrefixTagResolver) }
        .build()

    /**
     * Loads the `lang.yml` file from disk, creating the file if it does not exist.
     *
     * It supports upgrading lang files by adding new keys if necessary, and warns server admins
     * if older/unknown keys are present in the lang file.
     *
     * Currently, it does not support updating the values of existing keys. If it is necessary to
     * overwrite the value of an existing key, it may be preferable to simply create a new key, or
     * to implement this at the plugin-level.
     *
     * Note: This operation is not thread safe, since [RosettaLang] is backed by a [java.util.HashMap]!
     *
     * @throws java.io.IOException if any I/O operation fails
     */
    fun reloadLangFile() {
        if (!dataFolder.exists())
            dataFolder.createDirectories()
        val langFilePath = dataFolder.resolve("lang.yml")
        val langFileExists = langFilePath.exists()
        if (!langFileExists) {
            Files.copy(defaultLangFile, langFilePath)
        }

        langMap.putAll(yaml.load<Map<String, String>>(langFilePath.reader()))

        if (langFileExists) {
            val defaultLangMap = yaml.load<Map<String, String>>(defaultLangFile)
            val defaultKeys = defaultLangMap.keys
            val storedKeys = langMap.keys

            val unknownKeys = storedKeys.subtract(defaultKeys)
            if (unknownKeys.isNotEmpty()) {
                platform.warn("Unknown keys found in lang.yml file: " +
                        "${unknownKeys.joinToString(", ")}\n" +
                        "Please remove these keys!")
            }

            // TODO: Support lang file upgrades by accepting arrays of Strings?
            //       If the stored value matches any in the array, replace it with the latest copy.
            //       Though, it may be more prudent for plugin developers to just create new lang keys

            val newKeys = defaultKeys.subtract(storedKeys)
            if (newKeys.isNotEmpty()) {
                platform.warn("Adding new keys to lang.yml file: ${newKeys.joinToString(", ")}")
                newKeys.forEach { langMap[it] = defaultLangMap[it]!! }
                yaml.dump(langMap, langFilePath.writer())
            }
        }
    }

    /**
     * Returns the raw String value for the provided Lang key, or `null` otherwise.
     *
     * @param key the Lang key
     * @return the value or `null` if key does not exist
     */
    fun getRaw(key: String): String? = langMap[key]

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the default MiniMessage instance.
     *
     * @param key the Lang key
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    operator fun get(key: String): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null)
    }


    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the default MiniMessage instance.
     *
     * Accepts tag resolvers to parse tags of the form `<key>`.
     *
     * Tags will be resolved from the resolver parameters before the resolver provided in the builder is used.
     *
     * @param key the Lang key
     * @param tagResolvers a series of tag resolvers to apply extra tags from, last specified taking priority
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(key: String, vararg tagResolvers: TagResolver): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, *tagResolvers)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the default MiniMessage instance.
     *
     * Accepts a target.
     *
     * @param key the Lang key
     * @param target the target of the deserialization
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(key: String, target: Pointered): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, target)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the default MiniMessage instance.
     *
     * Accepts a target and tag resolvers to parse tags of the form `<key>`.
     *
     * Tags will be resolved from the resolver parameters before the resolver provided in the builder is used.
     *
     * @param key the Lang key
     * @param target the target of the deserialization
     * @param tagResolvers a series of tag resolvers to apply extra tags from, last specified taking priority
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(key: String, target: Pointered, vararg tagResolvers: TagResolver): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, target, *tagResolvers)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the provided MiniMessage instance.
     *
     * @param miniMessage an instance of MiniMessage
     * @param key the Lang key
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(miniMessage: MiniMessage, key: String): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the provided MiniMessage instance.
     *
     * Accepts tag resolvers to parse tags of the form `<key>`.
     *
     * Tags will be resolved from the resolver parameters before the resolver provided in the builder is used.
     *
     * @param miniMessage an instance of MiniMessage
     * @param key the Lang key
     * @param tagResolvers a series of tag resolvers to apply extra tags from, last specified taking priority
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(miniMessage: MiniMessage, key: String, vararg tagResolvers: TagResolver): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, *tagResolvers)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the provided MiniMessage instance.
     *
     * Accepts a target.
     *
     * @param miniMessage an instance of MiniMessage
     * @param key the Lang key
     * @param target the target of the deserialization
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(miniMessage: MiniMessage, key: String, target: Pointered): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, target)
    }

    /**
     * Returns a [net.kyori.adventure.text.Component] for the provided Lang key, deserialised using
     * the provided MiniMessage instance.
     *
     * Accepts a target and tag resolvers to parse tags of the form `<key>`.
     *
     * Tags will be resolved from the resolver parameters before the resolver provided in the builder is used.
     *
     * @param miniMessage an instance of MiniMessage
     * @param key the Lang key
     * @param target the target of the deserialization
     * @param tagResolvers a series of tag resolvers to apply extra tags from, last specified taking priority
     *
     * @return the deserialised [net.kyori.adventure.text.Component] or `null` if key does not exist
     * @see net.kyori.adventure.text.minimessage.MiniMessage.deserialize
     */
    fun get(miniMessage: MiniMessage, key: String, target: Pointered, vararg tagResolvers: TagResolver): Component? {
        return miniMessage.deserialize(langMap[key] ?: return null, target, *tagResolvers)
    }
}
