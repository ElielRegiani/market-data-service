package investment_intelligence_platform.market_data_service.domain.service

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant

class MarketDataNormalizerTest {
    private val normalizer = MarketDataNormalizer()

    @Test
    fun `should normalize provider payload into canonical snapshot`() {
        val external = ExternalMarketData(
            symbol = "petr4",
            price = 32.45,
            volume = 1234567.0,
            timestamp = Instant.parse("2026-03-30T13:00:00Z")
        )

        val normalized = normalizer.normalize(external)

        assertEquals("PETR4", normalized.symbol)
        assertEquals(32.45, normalized.price)
        assertEquals(1234567.0, normalized.volume)
        assertNull(normalized.indicators.sma)
        assertNull(normalized.indicators.ema)
        assertNull(normalized.indicators.rsi)
        assertNull(normalized.indicators.volatility)
    }
}

