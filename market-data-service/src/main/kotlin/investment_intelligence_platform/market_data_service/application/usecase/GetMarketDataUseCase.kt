package investment_intelligence_platform.market_data_service.application.usecase

import investment_intelligence_platform.market_data_service.application.dto.MarketDataHistoryItemResponse
import investment_intelligence_platform.market_data_service.application.dto.MarketDataResponse
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataReadRepositoryPort
import org.springframework.stereotype.Service

@Service
class GetMarketDataUseCase(
    private val readRepository: MarketDataReadRepositoryPort
) {
    fun getCurrent(symbol: String): MarketDataResponse? {
        val latest = readRepository.findLatest(symbol) ?: return null
        val trend = when {
            latest.indicators.ema != null && latest.price >= latest.indicators.ema -> "UP"
            latest.indicators.ema != null -> "DOWN"
            else -> "NEUTRAL"
        }
        return MarketDataResponse(
            symbol = latest.symbol,
            price = latest.price,
            rsi = latest.indicators.rsi,
            trend = trend,
            timestamp = latest.timestamp
        )
    }

    fun getHistory(symbol: String, limit: Int = 100): List<MarketDataHistoryItemResponse> =
        readRepository.findHistory(symbol, limit).map {
            MarketDataHistoryItemResponse(
                symbol = it.symbol,
                price = it.price,
                volume = it.volume,
                sma = it.indicators.sma,
                ema = it.indicators.ema,
                rsi = it.indicators.rsi,
                volatility = it.indicators.volatility,
                timestamp = it.timestamp
            )
        }
}

