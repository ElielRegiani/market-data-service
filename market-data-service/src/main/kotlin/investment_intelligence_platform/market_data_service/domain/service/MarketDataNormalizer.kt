package investment_intelligence_platform.market_data_service.domain.service

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import org.springframework.stereotype.Component

@Component
class MarketDataNormalizer {
    fun normalize(external: ExternalMarketData): MarketDataSnapshot =
        MarketDataSnapshot(
            symbol = external.symbol.uppercase(),
            price = external.price,
            volume = external.volume,
            timestamp = external.timestamp,
            indicators = MarketDataIndicators(
                sma = null,
                ema = null,
                rsi = null,
                volatility = null
            )
        )
}

