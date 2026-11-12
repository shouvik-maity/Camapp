package com.yourapp.hiddencam

import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.fusion.RiskLevel
import com.yourapp.hiddencam.fusion.SignalType
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskScorerTest {
    @Test
    fun lowRiskBelowThreshold() {
        val findings = listOf(
            Finding(
                id = "low",
                type = SignalType.SSID,
                title = "Low case",
                detail = "Minimal signal",
                weight = 12,
            ),
        )

        val (score, level) = com.yourapp.hiddencam.scan.DefaultRiskScorer().score(findings)
        assertEquals(12, score)
        assertEquals(RiskLevel.LOW, level)
    }

    @Test
    fun mediumRiskAtThreshold() {
        val findings = listOf(
            Finding(
                id = "medium",
                type = SignalType.BLE,
                title = "Medium case",
                detail = "Moderate signal",
                weight = 32,
            ),
        )

        val (score, level) = com.yourapp.hiddencam.scan.DefaultRiskScorer().score(findings)
        assertEquals(32, score)
        assertEquals(RiskLevel.MEDIUM, level)
    }

    @Test
    fun highRiskAboveThreshold() {
        val findings = listOf(
            Finding(
                id = "high",
                type = SignalType.LAN_DEVICE,
                title = "High case",
                detail = "Strong signal",
                weight = 80,
            ),
        )

        val (score, level) = com.yourapp.hiddencam.scan.DefaultRiskScorer().score(findings)
        assertEquals(80, score)
        assertEquals(RiskLevel.HIGH, level)
    }
}
