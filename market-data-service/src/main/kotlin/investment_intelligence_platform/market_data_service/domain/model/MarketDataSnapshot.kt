package investment_intelligence_platform.market_data_service.domain.model

import java.time.Instant

/**
 * Canonical snapshot for the platform. This is the data that we enrich and persist.
 */
data class MarketDataSnapshot(
    val symbol: String,
    val price: Double,
    val volume: Double,
    val timestamp: Instant,
    val indicators: MarketDataIndicators = MarketDataIndicators(
        sma = null,
        ema = null,
        rsi = null,
        volatility = null
    )
)

