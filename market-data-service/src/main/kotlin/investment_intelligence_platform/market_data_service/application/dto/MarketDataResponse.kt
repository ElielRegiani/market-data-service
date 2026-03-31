package investment_intelligence_platform.market_data_service.application.dto

import java.time.Instant

data class MarketDataResponse(
    val symbol: String,
    val price: Double,
    val rsi: Double?,
    val trend: String,
    val timestamp: Instant
)

data class MarketDataHistoryItemResponse(
    val symbol: String,
    val price: Double,
    val volume: Double,
    val sma: Double?,
    val ema: Double?,
    val rsi: Double?,
    val volatility: Double?,
    val timestamp: Instant
)

