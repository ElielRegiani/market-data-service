//package investment_intelligence_platform.market_data_service.infrastructure.messaging
//
//import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
//import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory
//import org.springframework.kafka.test.EmbeddedKafkaBroker
//import org.springframework.kafka.test.context.EmbeddedKafka
//import org.springframework.kafka.test.utils.KafkaTestUtils
//import org.springframework.test.context.DynamicPropertyRegistry
//import org.springframework.test.context.DynamicPropertySource
//import org.springframework.test.context.TestPropertySource
//import java.time.Duration
//import java.time.Instant
//import java.util.UUID
//
//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = ["market-data-updated"])
//@TestPropertySource(
//    properties = [
//        "app.kafka.topics.market-data-updated=market-data-updated",
//        "app.market-data.job.enabled=false",
//        "spring.flyway.enabled=false"
//    ]
//)
//class KafkaMarketDataEventPublisherIntegrationTest {
//
//    @Autowired
//    lateinit var publisher: KafkaMarketDataEventPublisher
//
//    @Autowired
//    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker
//
//    @Test
//    fun `should publish market data event to kafka topic`() {
//        val snapshot = MarketDataSnapshot(
//            symbol = "PETR4",
//            price = 32.45,
//            volume = 1234567.0,
//            timestamp = Instant.parse("2026-03-30T13:00:00Z"),
//            indicators = MarketDataIndicators(
//                sma = 31.90,
//                ema = 31.80,
//                rsi = 68.0,
//                volatility = 0.02
//            )
//        )
//
//        val consumerProps = KafkaTestUtils.consumerProps(
//            "test-group-${UUID.randomUUID()}",
//            "true",
//            embeddedKafkaBroker
//        ).toMutableMap()
//        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//
//        val consumerFactory = DefaultKafkaConsumerFactory(
//            consumerProps,
//            StringDeserializer(),
//            StringDeserializer()
//        )
//        val consumer = consumerFactory.createConsumer()
//        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "market-data-updated")
//
//        publisher.publishMarketDataUpdated(snapshot)
//
//        val record = KafkaTestUtils.getSingleRecord(
//            consumer,
//            "market-data-updated",
//            Duration.ofSeconds(10)
//        )
//        assertTrue(record.key() == "PETR4")
//        assertTrue(record.value().contains("\"symbol\":\"PETR4\""))
//    }
//
//    companion object {
//        @JvmStatic
//        @DynamicPropertySource
//        fun kafkaProps(registry: DynamicPropertyRegistry) {
//            registry.add("spring.kafka.bootstrap-servers") { System.getProperty("spring.embedded.kafka.brokers") }
//        }
//    }
//}
//
