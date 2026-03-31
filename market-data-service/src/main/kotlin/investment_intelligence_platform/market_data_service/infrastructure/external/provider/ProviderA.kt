package investment_intelligence_platform.market_data_service.infrastructure.external.provider

import investment_intelligence_platform.market_data_service.domain.model.ExternalMarketData
import investment_intelligence_platform.market_data_service.domain.service.MarketDataProvider
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
class ProviderA : MarketDataProvider {
    override val name: String = "stubA"

    @Retry(name = "marketDataProviders")
    @CircuitBreaker(name = "marketDataProviders")
    override fun fetch(symbol: String): ExternalMarketData {
        if (Random.nextInt(0, 10) < 2) {
            throw IllegalStateException("Simulated providerA failure")
        }
        return ExternalMarketData(
            symbol = symbol.uppercase(),
            price = Random.nextDouble(10.0, 300.0),
            volume = Random.nextDouble(100_000.0, 2_000_000.0),
            timestamp = Instant.now()
        )
    }
}

