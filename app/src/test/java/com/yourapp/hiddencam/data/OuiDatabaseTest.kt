package com.yourapp.hiddencam.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OuiDatabaseTest {
    @Test
    fun lookupVendorReturnsKnownCameraManufacturers() {
        assertEquals("Hikvision", OuiDatabase.lookupVendor("00:17:56:7A:28:99"))
        assertEquals("Dahua", OuiDatabase.lookupVendor("00:18:82:12:33:44"))
        assertEquals("Ezviz", OuiDatabase.lookupVendor("00:12:34:56:78:90"))
        assertEquals("Tuya", OuiDatabase.lookupVendor("E4:5F:01:11:22:33"))
    }

    @Test
    fun lookupVendorReturnsNullForUnknownMacs() {
        assertNull(OuiDatabase.lookupVendor("AA:BB:CC:DD:EE:FF"))
        assertNull(OuiDatabase.lookupVendor("10:20:30:40:50:60"))
    }
}
