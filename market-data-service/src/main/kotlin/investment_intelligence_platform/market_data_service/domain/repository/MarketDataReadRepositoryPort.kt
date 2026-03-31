package investment_intelligence_platform.market_data_service.domain.repository

import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot

interface MarketDataReadRepositoryPort {
    fun findLatest(symbol: String): MarketDataSnapshot?
    fun findHistory(symbol: String, limit: Int): List<MarketDataSnapshot>
}

