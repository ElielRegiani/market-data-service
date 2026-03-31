package investment_intelligence_platform.market_data_service.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "market_data")
class MarketDataEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 32)
    var symbol: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var volume: Double,

    @Column(nullable = false)
    var ts: Instant,

    @Column
    var sma: Double? = null,

    @Column
    var ema: Double? = null,

    @Column
    var rsi: Double? = null,

    @Column
    var volatility: Double? = null
)

