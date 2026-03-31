package investment_intelligence_platform.market_data_service.domain.repository

import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot

/**
 * Port to persist enriched snapshots.
 */
interface MarketDataWriteRepositoryPort {
    fun saveSnapshot(snapshot: MarketDataSnapshot)
}

