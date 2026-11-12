package com.yourapp.hiddencam

import com.yourapp.hiddencam.scan.RssiSmoother
import org.junit.Assert.assertTrue
import org.junit.Test

class RssiSmootherTest {
    @Test
    fun medianFilterRejectsSpikes() {
        val smoother = RssiSmoother()
        val filtered = smoother.filterAndSmooth("aa", -60)
        val filteredSpike = smoother.filterAndSmooth("aa", -20)
        val filteredStable = smoother.filterAndSmooth("aa", -50)
        assertTrue(filtered.second <= -50.0 || filteredStable.second <= -50.0)
        assertTrue(filteredSpike.second >= filtered.second)
    }

    @Test
    fun emaSmoothsTowardRecentReading() {
        val smoother = RssiSmoother(alpha = 0.25)
        val first = smoother.filterAndSmooth("bb", -70)
        val second = smoother.filterAndSmooth("bb", -60)
        assertTrue(second.second > first.second)
    }
}
