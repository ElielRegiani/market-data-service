package investment_intelligence_platform.market_data_service.domain.service

import investment_intelligence_platform.market_data_service.domain.model.MarketDataIndicators
import org.springframework.stereotype.Component
import kotlin.math.pow
import kotlin.math.sqrt

@Component
class TechnicalIndicatorsCalculator {

    fun calculate(history: List<Double>, currentPrice: Double): MarketDataIndicators {
        val series = (history + currentPrice).filter { it > 0.0 }
        return MarketDataIndicators(
            sma = sma(series, 14),
            ema = ema(series, 14),
            rsi = rsi(series, 14),
            volatility = volatility(series, 14)
        )
    }

    fun sma(prices: List<Double>, period: Int): Double? {
        if (prices.size < period) return null
        return prices.takeLast(period).average()
    }

    fun ema(prices: List<Double>, period: Int): Double? {
        if (prices.size < period) return null
        val k = 2.0 / (period + 1.0)
        var ema = prices.take(period).average()
        prices.drop(period).forEach { price ->
            ema = (price * k) + (ema * (1.0 - k))
        }
        return ema
    }

    fun rsi(prices: List<Double>, period: Int): Double? {
        if (prices.size < period + 1) return null
        val changes = prices.zipWithNext { a, b -> b - a }.takeLast(period)
        val gains = changes.filter { it > 0.0 }.sum()
        val losses = -changes.filter { it < 0.0 }.sum()
        if (losses == 0.0) return 100.0
        val rs = (gains / period) / (losses / period)
        return 100.0 - (100.0 / (1.0 + rs))
    }

    fun volatility(prices: List<Double>, period: Int): Double? {
        if (prices.size < period + 1) return null
        val returns = prices.takeLast(period + 1).zipWithNext { a, b ->
            if (a == 0.0) 0.0 else (b - a) / a
        }
        val mean = returns.average()
        val variance = returns.map { (it - mean).pow(2) }.average()
        return sqrt(variance)
    }
}

