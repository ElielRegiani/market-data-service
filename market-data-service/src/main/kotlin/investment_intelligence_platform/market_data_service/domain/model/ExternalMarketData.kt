package investment_intelligence_platform.market_data_service.domain.model

import java.time.Instant

/**
 * Generic external representation returned by market data providers.
 * Normalization happens in the application layer.
 */
data class ExternalMarketData(
    val symbol: String,
    val price: Double,
    val volume: Double,
    val timestamp: Instant
)

