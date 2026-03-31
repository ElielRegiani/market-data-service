package investment_intelligence_platform.market_data_service.domain.service

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TechnicalIndicatorsCalculatorTest {
    private val calculator = TechnicalIndicatorsCalculator()

    @Test
    fun `should calculate all required indicators`() {
        val history = (1..30).map { 90.0 + it }
        val result = calculator.calculate(history, 125.0)

        assertNotNull(result.sma)
        assertNotNull(result.ema)
        assertNotNull(result.rsi)
        assertNotNull(result.volatility)
    }

    @Test
    fun `rsi should stay in valid range`() {
        val prices = listOf(44.0, 44.15, 43.9, 44.35, 44.8, 45.1, 44.7, 45.3, 45.0, 45.5, 45.8, 46.2, 46.0, 46.5, 46.8, 47.2)
        val rsi = calculator.rsi(prices, 14)
        assertTrue(rsi != null && rsi in 0.0..100.0)
    }
}

