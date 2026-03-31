package investment_intelligence_platform.market_data_service.infrastructure.messaging

import investment_intelligence_platform.market_data_service.domain.messaging.MarketDataEventPublisherPort
import investment_intelligence_platform.market_data_service.domain.model.MarketDataSnapshot
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaMarketDataEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.kafka.topics.market-data-updated}") private val topic: String
) : MarketDataEventPublisherPort {
    override fun publishMarketDataUpdated(snapshot: MarketDataSnapshot) {
        val event = MarketDataUpdatedEvent(
            symbol = snapshot.symbol,
            price = snapshot.price,
            indicators = IndicatorsPayload(
                rsi = snapshot.indicators.rsi,
                sma = snapshot.indicators.sma,
                ema = snapshot.indicators.ema,
                volatility = snapshot.indicators.volatility
            ),
            timestamp = snapshot.timestamp
        )
        kafkaTemplate.send(topic, snapshot.symbol, event)
    }
}

