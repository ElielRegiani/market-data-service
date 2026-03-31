package investment_intelligence_platform.market_data_service.entrypoint.batch

import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataHistoryRepositoryPort
import investment_intelligence_platform.market_data_service.domain.repository.MarketDataWriteRepositoryPort
import investment_intelligence_platform.market_data_service.domain.service.MarketDataProvider
import investment_intelligence_platform.market_data_service.domain.service.TechnicalIndicatorsCalculator
import investment_intelligence_platform.market_data_service.domain.messaging.MarketDataEventPublisherPort
import investment_intelligence_platform.market_data_service.infrastructure.config.AppMarketDataProperties
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.PlatformTransactionManager
import java.time.Instant

@Configuration
@EnableBatchProcessing
@EnableScheduling
class MarketDataJobConfig(
    private val appProperties: AppMarketDataProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun marketDataJob(
        jobRepository: JobRepository,
        fetchStep: Step,
        normalizeStep: Step,
        enrichStep: Step,
        persistStep: Step,
        publishStep: Step,
        meterRegistry: MeterRegistry
    ): Job = JobBuilder("MarketDataJob", jobRepository)
        .start(fetchStep)
        .next(normalizeStep)
        .next(enrichStep)
        .next(persistStep)
        .next(publishStep)
        .listener(object : JobExecutionListener {
            override fun beforeJob(jobExecution: JobExecution) = Unit
            override fun afterJob(jobExecution: JobExecution) {
                val durationMs = (jobExecution.endTime?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis()) -
                    (jobExecution.startTime?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis())
                meterRegistry.timer("market_data.job.execution").record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS)
            }
        })
        .build()

    @Bean
    fun fetchStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        providers: List<MarketDataProvider>,
        state: MarketDataBatchState,
        meterRegistry: MeterRegistry
    ): Step = StepBuilder("fetchStep", jobRepository)
        .tasklet({ _, _ ->
            state.clear()
            val activeNames = appProperties.providers.active.toSet()
            val activeProviders = if (activeNames.isEmpty()) providers else providers.filter { activeNames.contains(it.name) }
            state.startedAt = Instant.now()
            appProperties.symbols.forEach { symbol ->
                var loaded = false
                activeProviders.forEach { provider ->
                    if (!loaded) {
                        try {
                            state.fetched[symbol] = provider.fetch(symbol)
                            loaded = true
                        } catch (e: Exception) {
                            meterRegistry.counter("market_data.provider.failures", "provider", provider.name).increment()
                            logger.warn("provider_failure provider={} symbol={} message={}", provider.name, symbol, e.message)
                        }
                    }
                }
            }
            RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun normalizeStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        state: MarketDataBatchState
    ): Step = StepBuilder("normalizeStep", jobRepository)
        .tasklet({ _, _ ->
            state.fetched.forEach { (_, ext) ->
                state.normalized[ext.symbol] = MarketDataSnapshot(
                    symbol = ext.symbol,
                    price = ext.price,
                    volume = ext.volume,
                    timestamp = ext.timestamp,
                    indicators = MarketDataIndicators(null, null, null, null)
                )
            }
            RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun enrichStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        state: MarketDataBatchState,
        historyRepository: MarketDataHistoryRepositoryPort,
        indicatorsCalculator: TechnicalIndicatorsCalculator
    ): Step = StepBuilder("enrichStep", jobRepository)
        .tasklet({ _, _ ->
            state.normalized.forEach { (_, snapshot) ->
                val history = historyRepository.loadRecentPrices(snapshot.symbol, 200, snapshot.timestamp)
                val indicators = indicatorsCalculator.calculate(history, snapshot.price)
                state.enriched[snapshot.symbol] = snapshot.copy(indicators = indicators)
            }
            RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun persistStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        state: MarketDataBatchState,
        writeRepository: MarketDataWriteRepositoryPort
    ): Step = StepBuilder("persistStep", jobRepository)
        .tasklet({ _, _ ->
            state.enriched.forEach { (_, snapshot) -> writeRepository.saveSnapshot(snapshot) }
            RepeatStatus.FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun publishStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        state: MarketDataBatchState,
        eventPublisher: MarketDataEventPublisherPort
    ): Step = StepBuilder("publishStep", jobRepository)
        .tasklet({ _, _ ->
            state.enriched.forEach { (_, snapshot) -> eventPublisher.publishMarketDataUpdated(snapshot) }
            RepeatStatus.FINISHED
        }, transactionManager)
        .build()
}

