package investment_intelligence_platform.market_data_service.infrastructure.persistence

import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataHistoryRepositoryPort
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataReadRepositoryPort
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataWriteRepositoryPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class MarketDataRepositoryAdapter(
    private val repository: SpringDataMarketDataRepository
) : MarketDataWriteRepositoryPort, MarketDataReadRepositoryPort, MarketDataHistoryRepositoryPort {

    @Transactional
    override fun saveSnapshot(snapshot: MarketDataSnapshot) {
        val existing = repository.findBySymbolAndTs(snapshot.symbol, snapshot.timestamp)
        if (existing != null) {
            existing.price = snapshot.price
            existing.volume = snapshot.volume
            existing.sma = snapshot.indicators.sma
            existing.ema = snapshot.indicators.ema
            existing.rsi = snapshot.indicators.rsi
            existing.volatility = snapshot.indicators.volatility
            repository.save(existing)
            return
        }
        repository.save(
            MarketDataEntity(
                symbol = snapshot.symbol,
                price = snapshot.price,
                volume = snapshot.volume,
                ts = snapshot.timestamp,
                sma = snapshot.indicators.sma,
                ema = snapshot.indicators.ema,
                rsi = snapshot.indicators.rsi,
                volatility = snapshot.indicators.volatility
            )
        )
    }

    override fun findLatest(symbol: String): MarketDataSnapshot? =
        repository.findTop1BySymbolOrderByTsDesc(symbol)?.toDomain()

    override fun findHistory(symbol: String, limit: Int): List<MarketDataSnapshot> =
        repository.findBySymbolOrderByTsDesc(symbol).take(limit).map { it.toDomain() }

    override fun loadRecentPrices(symbol: String, windowSize: Int, upToInclusive: Instant): List<Double> =
        repository.findTop200BySymbolAndTsLessThanEqualOrderByTsDesc(symbol, upToInclusive)
            .take(windowSize)
            .map { it.price }
            .reversed()

    private fun MarketDataEntity.toDomain() = MarketDataSnapshot(
        symbol = symbol,
        price = price,
        volume = volume,
        timestamp = ts,
        indicators = MarketDataIndicators(
            sma = sma,
            ema = ema,
            rsi = rsi,
            volatility = volatility
        )
    )
}

