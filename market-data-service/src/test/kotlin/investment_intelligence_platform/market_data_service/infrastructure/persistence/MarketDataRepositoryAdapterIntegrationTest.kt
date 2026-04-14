package investment_intelligence_platform.market_data_service.infrastructure.persistence

import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class MarketDataRepositoryAdapterIntegrationTest {

    @Autowired
    lateinit var adapter: MarketDataRepositoryAdapter

    @Test
    fun `should persist and read latest market data`() {
        val snapshot = MarketDataSnapshot(
            symbol = "PETR4",
            price = 32.45,
            volume = 1234567.0,
            timestamp = Instant.parse("2026-03-30T13:00:00Z"),
            indicators = MarketDataIndicators(
                sma = 31.9,
                ema = 31.8,
                rsi = 68.0,
                volatility = 0.02
            )
        )

        adapter.saveSnapshot(snapshot)
        val latest = adapter.findLatest("PETR4")

        assertNotNull(latest)
        assertEquals(32.45, latest?.price)
        assertEquals(68.0, latest?.indicators?.rsi)
    }

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
            .withDatabaseName("market_data")
            .withUsername("market_data")
            .withPassword("market_data")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "validate" }
            registry.add("spring.flyway.enabled") { "true" }
            registry.add("app.market-data.job.enabled") { "false" }
            registry.add("spring.kafka.bootstrap-servers") { "localhost:9092" }
        }
    }
}

