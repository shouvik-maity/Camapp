package com.yourapp.hiddencam.scan

import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.fusion.RiskLevel

class DefaultRiskScorer : RiskScorer {
    override fun score(findings: List<Finding>): Pair<Int, RiskLevel> {
        val total = findings
            .filterNot { it.dismissed }
            .sumOf { it.weight }
            .coerceAtMost(100)

        val level = when {
            total >= 70 -> RiskLevel.HIGH
            total >= 30 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return total to level
    }
}
