package investment_intelligence_platform.market_data_service.entrypoint.batch

import investment_intelligence_platform.market_data_service.infrastructure.config.AppMarketDataProperties
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MarketDataJobScheduler(
    private val jobLauncher: JobLauncher,
    @Qualifier("marketDataJob") private val marketDataJob: Job,
    private val appProperties: AppMarketDataProperties
) {
    @Scheduled(fixedDelayString = "\${app.market-data.job.fixed-delay}")
    fun schedule() {
        if (!appProperties.job.enabled) return
        val params = JobParametersBuilder()
            .addString("runId", UUID.randomUUID().toString())
            .addLong("ts", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(marketDataJob, params)
    }
}

