package investment_intelligence_platform.market_data_service.domain.service

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData

/**
 * Port to fetch market data from external providers.
 * Implementations live in infrastructure.
 */
interface MarketDataProvider {
    val name: String
    fun fetch(symbol: String): ExternalMarketData
}

