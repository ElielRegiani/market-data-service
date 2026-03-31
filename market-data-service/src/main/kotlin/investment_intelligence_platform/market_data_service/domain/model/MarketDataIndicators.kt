package investment_intelligence_platform.market_data_service.domain.model

data class MarketDataIndicators(
    val sma: Double?,
    val ema: Double?,
    val rsi: Double?,
    val volatility: Double?
)

