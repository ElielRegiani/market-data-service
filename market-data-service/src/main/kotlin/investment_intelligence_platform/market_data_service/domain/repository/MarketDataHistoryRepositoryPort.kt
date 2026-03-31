package investment_intelligence_platform.market_data_service.domain.repository

import java.time.Instant

/**
 * Port to load historical prices for technical indicators.
 */
interface MarketDataHistoryRepositoryPort {
    fun loadRecentPrices(symbol: String, windowSize: Int, upToInclusive: Instant): List<Double>
}

