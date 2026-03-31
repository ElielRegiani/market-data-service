package investment_intelligence_platform.market_data_service.domain.messaging

import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot

/**
 * Port to publish market-data-updated events (Kafka).
 */
interface MarketDataEventPublisherPort {
    fun publishMarketDataUpdated(snapshot: MarketDataSnapshot)
}

