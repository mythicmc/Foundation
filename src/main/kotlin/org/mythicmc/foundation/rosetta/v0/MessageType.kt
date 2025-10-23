package org.mythicmc.foundation.rosetta.v0

/**
 * Various predefined types of system messages, used with [Rosetta] to retrieve prefixes for
 * messages, and colour schemes for such messages.
 *
 * Server owners can override the colours of these types by modifying the `rosetta-v0/prefix.yml`
 * file in Foundation's data folder. However, the intents conveyed by the headers remain fixed.
 */
enum class MessageType {
    /**
     * Informational messages, typically blue with the `Info` header.
     */
    INFO,

    /**
     * Messages indicating success, typically green with the `Success` header.
     */
    SUCCESS,

    /**
     * Warning messages, typically yellow with the `Warning` header.
     */
    WARNING,

    /**
     * Error messages, typically red with the `Error` header.
     */
    ERROR,
}
