package investment_intelligence_platform.market_data_service.application.usecase

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import investment_intelligence_platform.market_data_service.domain.service.TechnicalIndicatorsCalculator
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataHistoryRepositoryPort
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataWriteRepositoryPort
import investment_intelligence_platform.market_data_service.domain.service.MarketDataProvider
import investment_intelligence_platform.market_data_service.domain.messaging.MarketDataEventPublisherPort
import investment_intelligence_platform.market_data_service.infrastructure.config.AppMarketDataProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

data class RefreshMarketDataUseCaseResult(
    val symbol: String,
    val fetchedFromProvider: String?,
    val persisted: Boolean
)

/**
 * Application orchestration:
 * 1) fetch with provider failover
 * 2) normalize into MarketDataSnapshot
 * 3) enrich (indicators) - placeholder until calculator is implemented
 * 4) persist
 * 5) publish event
 */
@Service
class RefreshMarketDataUseCase(
    private val providers: List<MarketDataProvider>,
    private val historyRepository: MarketDataHistoryRepositoryPort,
    private val writeRepository: MarketDataWriteRepositoryPort,
    private val eventPublisher: MarketDataEventPublisherPort,
    private val indicatorsCalculator: TechnicalIndicatorsCalculator,
    private val appMarketDataProperties: AppMarketDataProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(symbol: String, now: Instant = Instant.now()): RefreshMarketDataUseCaseResult {
        val providerErrors = mutableListOf<Throwable>()
        var fetchedFromProvider: String? = null
        var external: ExternalMarketData? = null

        val activeNames = appMarketDataProperties.providers.active.map { it.trim() }.filter { it.isNotBlank() }.toSet()
        val activeProviders = if (activeNames.isEmpty()) providers else providers.filter { activeNames.contains(it.name) }

        for (provider in activeProviders) {
            try {
                external = provider.fetch(symbol)
                fetchedFromProvider = provider.name
                break
            } catch (e: Throwable) {
                logger.warn("provider_failure provider={} symbol={} message={}", provider.name, symbol, e.message)
                providerErrors.add(e)
            }
        }

        // If all providers failed, propagate a meaningful error for the job runner to handle.
        val externalNonNull = external ?: throw IllegalStateException(
            "All providers failed for symbol=$symbol. Errors=${providerErrors.size}. ActiveProviders=${activeProviders.map { it.name }}"
        )

        val history = historyRepository.loadRecentPrices(
            symbol = externalNonNull.symbol,
            windowSize = 200,
            upToInclusive = now
        )
        val indicators = indicatorsCalculator.calculate(history, externalNonNull.price)

        val snapshot = MarketDataSnapshot(
            symbol = externalNonNull.symbol,
            price = externalNonNull.price,
            volume = externalNonNull.volume,
            timestamp = externalNonNull.timestamp,
            indicators = MarketDataIndicators(
                sma = indicators.sma,
                ema = indicators.ema,
                rsi = indicators.rsi,
                volatility = indicators.volatility
            )
        )
        writeRepository.saveSnapshot(snapshot)
        eventPublisher.publishMarketDataUpdated(snapshot)

        return RefreshMarketDataUseCaseResult(
            symbol = symbol,
            fetchedFromProvider = fetchedFromProvider,
            persisted = true
        )
    }
}

