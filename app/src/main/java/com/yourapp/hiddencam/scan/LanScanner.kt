package com.yourapp.hiddencam.scan

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL

typealias HostProbe = Pair<String, Int>

class AndroidLanScanner(
    private val context: Context,
) : LanScanner {
    override fun sweep(): Flow<LanDevice> = flow {
        val localHost = detectLocalHost() ?: return@flow
        val subnetBase = localHost.substringBeforeLast('.')
        val hosts = (1..254)
            .asSequence()
            .map { "$subnetBase.$it" }
            .filter { it != localHost }
            .toList()

        val results = mutableListOf<LanDevice>()
        for (host in hosts) {
            val device = probeHost(host)
            if (device != null) {
                results += device
                emit(device)
            }
        }

        if (results.isEmpty()) {
            emit(LanDevice(host = "network-isolated", port = 0, title = "network may isolate devices (hotel/guest Wi‑Fi)", score = 0))
        }
    }.flowOn(Dispatchers.IO)

    private fun detectLocalHost(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null
        while (interfaces.hasMoreElements()) {
            val iface = interfaces.nextElement()
            if (!iface.isUp || iface.isLoopback || iface.isVirtual) continue
            val addresses = iface.inetAddresses ?: continue
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (address is Inet4Address && !address.isLoopbackAddress && !address.isLinkLocalAddress) {
                    return address.hostAddress
                }
            }
        }
        return null
    }

    private fun probeHost(host: String): LanDevice? {
        val openPorts = mutableListOf<Int>()
        for (port in listOf(554, 8554, 80, 8080, 8000, 37777, 34567)) {
            if (isOpen(host, port, 300)) {
                openPorts += port
            }
        }

        if (openPorts.isEmpty()) return null

        var title: String? = null
        var serverHeader: String? = null
        var rtspBanner: String? = null
        var onvif = false

        if (openPorts.contains(80) || openPorts.contains(8080)) {
            val port = if (openPorts.contains(80)) 80 else 8080
            val response = fetchHttpMetadata(host, port)
            title = response.first
            serverHeader = response.second
        }

        if (openPorts.contains(554)) {
            rtspBanner = fetchRtspBanner(host)
        }

        if (openPorts.contains(8554)) {
            onvif = true
        }

        val score = buildScore(openPorts, title, serverHeader, rtspBanner, onvif)
        val port = openPorts.firstOrNull() ?: 0
        return LanDevice(
            host = host,
            port = port,
            title = title,
            serverHeader = serverHeader,
            rtspBanner = rtspBanner,
            onvif = onvif,
            score = score,
        )
    }

    private fun buildScore(
        openPorts: List<Int>,
        title: String?,
        serverHeader: String?,
        rtspBanner: String?,
        onvif: Boolean,
    ): Int {
        var score = 0
        if (openPorts.contains(554)) score += 35
        if (onvif) score += 30
        if (title != null || serverHeader != null) score += 20
        if (serverHeader?.contains("camera", ignoreCase = true) == true || title?.contains("camera", ignoreCase = true) == true) score += 20
        if (rtspBanner != null) score += 15
        return score.coerceAtMost(60)
    }

    private fun isOpen(host: String, port: Int, timeoutMs: Int): Boolean =
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            socket.close()
            true
        } catch (_: Exception) {
            false
        }

    private fun fetchHttpMetadata(host: String, port: Int): Pair<String?, String?> {
        return try {
            val connection = URL("http://$host:$port").openConnection() as HttpURLConnection
            connection.connectTimeout = 300
            connection.readTimeout = 300
            connection.requestMethod = "GET"
            val bytes = connection.inputStream.use { it.readBytes() }
            val html = bytes.decodeToString()
            val title = Regex("<title>(.*?)</title>", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.getOrNull(1)?.trim()
            val server = connection.getHeaderField("Server")
            title to server
        } catch (_: Exception) {
            null to null
        }
    }

    private fun fetchRtspBanner(host: String): String? =
        try {
            val socket = Socket()
            socket.soTimeout = 300
            socket.connect(InetSocketAddress(host, 554), 300)
            val output = socket.getOutputStream()
            output.write("OPTIONS * RTSP/1.0\r\nCSeq: 1\r\n\r\n".toByteArray())
            output.flush()
            val input = socket.getInputStream().bufferedReader()
            val banner = input.readLine()
            socket.close()
            banner
        } catch (_: Exception) {
            null
        }
}
