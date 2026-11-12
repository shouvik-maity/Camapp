package com.yourapp.hiddencam.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignaturesTest {
    @Test
    fun cameraPatternsMatchPositiveExamples() {
        val positiveNames = listOf(
            "IPC-8841",
            "HD-1234",
            "CAM-1",
            "YI-ABCD",
            "TuyaCam",
            "SmartLife-Device",
            "EZVIZ-1",
            "V380-123",
            "ICSEE-ROOM",
            "WiFi_Cam_1",
            "Dahua-42",
            "Hikvision-7"
        )
        positiveNames.forEach { name ->
            assertTrue("Expected camera-like name to match: $name", Signatures.matchesCameraPattern(name))
        }
    }

    @Test
    fun cameraPatternsRejectNegativeExamples() {
        val negativeNames = listOf(
            "HomeWiFi",
            "OfficeNet",
            "LivingRoom5G",
            "GuestCafe",
            "MyRouter",
            "FiberBox",
            "NeighborWiFi",
            "CafeWifi",
            "PortableHotspot",
            "XiaomiMesh"
        )
        negativeNames.forEach { name ->
            assertFalse("Expected non-camera name not to match: $name", Signatures.matchesCameraPattern(name))
        }
    }
}
