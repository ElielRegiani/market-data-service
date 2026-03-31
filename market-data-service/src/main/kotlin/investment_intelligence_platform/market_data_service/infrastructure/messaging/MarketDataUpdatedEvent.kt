package investment_intelligence_platform.market_data_service.infrastructure.messaging

import java.time.Instant

data class MarketDataUpdatedEvent(
    val symbol: String,
    val price: Double,
    val indicators: IndicatorsPayload,
    val timestamp: Instant
)

data class IndicatorsPayload(
    val rsi: Double?,
    val sma: Double?,
    val ema: Double?,
    val volatility: Double?
)

