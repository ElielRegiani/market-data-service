package investment_intelligence_platform.market_data_service.entrypoint.rest

import investment_intelligence_platform.market_data_service.application.usecase.GetMarketDataUseCase
import investment_intelligence_platform.market_data_service.application.usecase.RefreshMarketDataUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/market-data")
class MarketDataController(
    private val getMarketDataUseCase: GetMarketDataUseCase,
    private val refreshMarketDataUseCase: RefreshMarketDataUseCase
) {
    @GetMapping("/{symbol}")
    fun getCurrent(@PathVariable symbol: String): ResponseEntity<Any> {
        val response = getMarketDataUseCase.getCurrent(symbol.uppercase()) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{symbol}/history")
    fun getHistory(
        @PathVariable symbol: String,
        @RequestParam(defaultValue = "100") limit: Int
    ): ResponseEntity<Any> {
        val response = getMarketDataUseCase.getHistory(symbol.uppercase(), limit.coerceIn(1, 1000))
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{symbol}/refresh")
    fun refresh(@PathVariable symbol: String): ResponseEntity<Any> =
        ResponseEntity.accepted().body(refreshMarketDataUseCase.execute(symbol.uppercase()))
}

