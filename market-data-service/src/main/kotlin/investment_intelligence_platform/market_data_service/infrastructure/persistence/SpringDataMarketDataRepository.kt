package investment_intelligence_platform.market_data_service.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataMarketDataRepository : JpaRepository<MarketDataEntity, Long> {
    fun findTop1BySymbolOrderByTsDesc(symbol: String): MarketDataEntity?
    fun findBySymbolOrderByTsDesc(symbol: String): List<MarketDataEntity>
    fun findBySymbolAndTs(symbol: String, ts: java.time.Instant): MarketDataEntity?
    fun findTop200BySymbolAndTsLessThanEqualOrderByTsDesc(symbol: String, ts: java.time.Instant): List<MarketDataEntity>
}

