package ru.lextop.miningpoolhub.vo

data class BalancePair(
    val current: Balance,
    val ticker: Ticker
) {
    val converted: Balance

    init {
        val price = ticker.otherStats.price ?: Double.NaN
        converted = current.copy(
            confirmed = price * current.confirmed,
            unconfirmed = price * current.unconfirmed,
            autoExchangeConfirmed = price * current.autoExchangeConfirmed,
            autoExchangeUnconfirmed = price * current.autoExchangeUnconfirmed,
            onExchange = price * current.onExchange
        )
    }
}
