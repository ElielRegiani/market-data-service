package investment_intelligence_platform.market_data_service.application.usecase

import investment_intelligence_platform.market_data_service.domain.messaging.MarketDataEventPublisherPort
import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataHistoryRepositoryPort
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataWriteRepositoryPort
import investment_intelligence_platform.market_data_service.domain.service.MarketDataNormalizer
import investment_intelligence_platform.market_data_service.domain.service.MarketDataProvider
import investment_intelligence_platform.market_data_service.domain.service.TechnicalIndicatorsCalculator
import investment_intelligence_platform.market_data_service.infrastructure.config.AppMarketDataProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class RefreshMarketDataUseCaseTest {
    @Test
    fun `should use failover provider and persist data`() {
        var persisted: MarketDataSnapshot? = null
        val providers = listOf(
            object : MarketDataProvider {
                override val name: String = "stubA"
                override fun fetch(symbol: String): ExternalMarketData = throw RuntimeException("fail")
            },
            object : MarketDataProvider {
                override val name: String = "stubB"
                override fun fetch(symbol: String): ExternalMarketData =
                    ExternalMarketData(symbol, 100.0, 1000.0, Instant.now())
            }
        )

        val useCase = RefreshMarketDataUseCase(
            providers = providers,
            historyRepository = object : MarketDataHistoryRepositoryPort {
                override fun loadRecentPrices(symbol: String, windowSize: Int, upToInclusive: Instant): List<Double> =
                    listOf(90.0, 95.0, 98.0, 99.0)
            },
            writeRepository = object : MarketDataWriteRepositoryPort {
                override fun saveSnapshot(snapshot: MarketDataSnapshot) {
                    persisted = snapshot
                }
            },
            eventPublisher = object : MarketDataEventPublisherPort {
                override fun publishMarketDataUpdated(snapshot: MarketDataSnapshot) = Unit
            },
            normalizer = MarketDataNormalizer(),
            indicatorsCalculator = TechnicalIndicatorsCalculator(),
            appMarketDataProperties = AppMarketDataProperties(
                symbols = listOf("PETR4"),
                providers = AppMarketDataProperties.ProvidersProperties(active = listOf("stubA", "stubB"))
            )
        )

        val result = useCase.execute("PETR4")

        assertEquals("stubB", result.fetchedFromProvider)
        assertEquals("PETR4", persisted?.symbol)
    }
}

