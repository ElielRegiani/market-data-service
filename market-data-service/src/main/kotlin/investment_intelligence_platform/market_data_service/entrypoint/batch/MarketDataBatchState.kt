package investment_intelligence_platform.market_data_service.entrypoint.batch

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class MarketDataBatchState {
    val fetched = ConcurrentHashMap<String, ExternalMarketData>()
    val normalized = ConcurrentHashMap<String, MarketDataSnapshot>()
    val enriched = ConcurrentHashMap<String, MarketDataSnapshot>()
    var startedAt: Instant = Instant.now()

    fun clear() {
        fetched.clear()
        normalized.clear()
        enriched.clear()
        startedAt = Instant.now()
    }
}

