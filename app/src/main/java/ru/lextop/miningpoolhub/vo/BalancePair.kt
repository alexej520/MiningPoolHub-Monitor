package ru.lextop.miningpoolhub.vo

data class BalancePair(
    val current: Balance,
    val converted: Balance?
)

operator fun Balance.times(factor: Double) = copy(
    confirmed = factor * confirmed,
    unconfirmed = factor * unconfirmed,
    autoExchangeConfirmed = factor * autoExchangeConfirmed,
    autoExchangeUnconfirmed = factor * autoExchangeUnconfirmed,
    onExchange = factor * onExchange
)

operator fun Balance.plus(other: Balance): Balance {
    val currency = currency
    if (currency == null || currency != other.currency)
        throw IllegalArgumentException()
    val result = Balance(
        coin = currency.id,
        confirmed = confirmed + other.confirmed,
        unconfirmed = unconfirmed + other.unconfirmed,
        autoExchangeConfirmed = autoExchangeConfirmed + other.autoExchangeConfirmed,
        autoExchangeUnconfirmed = autoExchangeUnconfirmed + other.autoExchangeUnconfirmed,
        onExchange = onExchange + other.onExchange
    )
    result.currency = currency
    return result
}
