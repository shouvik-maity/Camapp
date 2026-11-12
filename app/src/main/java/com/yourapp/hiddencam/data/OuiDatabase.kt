package com.yourapp.hiddencam.data

import java.io.InputStream

object OuiDatabase {
    private val defaultVendors = mapOf(
        "00:17:56" to "Hikvision",
        "00:18:82" to "Dahua",
        "00:24:6c" to "Hikvision",
        "00:0d:82" to "Dahua",
        "00:12:34" to "Ezviz",
        "e4:5f:01" to "Tuya",
        "24:5b:ab" to "Espressif",
        "00:16:3e" to "Reolink",
        "00:0c:43" to "Wyze",
        "00:0e:8f" to "Reolink",
    )

    fun lookupVendor(mac: String?): String? {
        val normalized = mac?.replace("-", ":")?.trim()?.lowercase() ?: return null
        val prefix = normalized
            .split(":")
            .take(3)
            .joinToString(":")
        return defaultVendors[prefix] ?: defaultVendors.entries.firstOrNull { (key, _) -> normalized.startsWith(key) }?.value
    }

    fun parseVendors(input: InputStream): Map<String, String> {
        val parsed = linkedMapOf<String, String>()
        input.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine
                val parts = trimmed.split(",")
                if (parts.size >= 2) {
                    val prefix = parts[0].trim().lowercase()
                    val vendor = parts[1].trim()
                    parsed[prefix] = vendor
                }
            }
        }
        return if (parsed.isNotEmpty()) parsed else defaultVendors
    }
}
