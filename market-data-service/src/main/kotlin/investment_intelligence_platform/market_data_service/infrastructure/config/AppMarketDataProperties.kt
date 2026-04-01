package investment_intelligence_platform.market_data_service.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app.market-data")
data class AppMarketDataProperties(
    val symbols: List<String> = emptyList(),
    val job: JobProperties = JobProperties(),
    val providers: ProvidersProperties = ProvidersProperties()
) {
    data class JobProperties(
        val enabled: Boolean = true,
        val cron: String = "0 10 18 * * MON-FRI",
        val zone: String = "America/Sao_Paulo"
    )

    data class ProvidersProperties(
        val active: List<String> = emptyList()
    )
}

@Configuration
@EnableConfigurationProperties(AppMarketDataProperties::class)
class AppPropertiesConfig

