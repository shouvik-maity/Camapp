package com.yourapp.hiddencam.data

import java.io.InputStream
import java.util.Locale

object Signatures {
    private val defaultPatterns = listOf(
        "^ipc",
        "^hd-",
        "cam",
        "^yi-",
        "tuya",
        "smartlife",
        "ezviz",
        "v380",
        "icsee",
        "wifi_cam",
        "^dahua",
        "^hik",
    )

    fun defaultPatterns(): List<String> = defaultPatterns.toList()

    fun patternWeight(ssid: String): Int {
        val cleaned = normalize(ssid)
        if (cleaned.isEmpty()) return 0
        return if (defaultPatterns.any { pattern -> Regex(pattern, RegexOption.IGNORE_CASE).containsMatchIn(cleaned) }) 25 else 0
    }

    fun matchesCameraPattern(ssid: String): Boolean = patternWeight(ssid) > 0

    fun parsePatterns(input: InputStream): List<String> {
        val text = input.bufferedReader().use { it.readText() }
        return Regex("\"([^\"]+)\"")
            .findAll(text)
            .map { it.groupValues[1] }
            .toList()
            .ifEmpty { defaultPatterns() }
    }

    private fun normalize(ssid: String): String = ssid.trim().lowercase(Locale.US)
}
