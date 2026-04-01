package investment_intelligence_platform.market_data_service.entrypoint.batch

import investment_intelligence_platform.market_data_service.infrastructure.config.AppMarketDataProperties
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MarketDataJobScheduler(
    private val jobLauncher: JobLauncher,
    @Qualifier("marketDataJob") private val marketDataJob: Job,
    private val appProperties: AppMarketDataProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(
        cron = "\${app.market-data.job.cron}",
        zone = "\${app.market-data.job.zone:America/Sao_Paulo}"
    )
    fun schedule() {
        if (!appProperties.job.enabled) return
        try {
            val params = JobParametersBuilder()
                .addString("runId", UUID.randomUUID().toString())
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters()
            jobLauncher.run(marketDataJob, params)
        } catch (e: Exception) {
            logger.error("market_data_job_schedule_failed message={}", e.message, e)
        }
    }
}

