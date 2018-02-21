package ru.lextop.miningpoolhub.vo

data class BalancePair(
    val current: Balance,
    val converted: Resource<Balance>
)

operator fun Balance.times(factor: Double) = copy(
    confirmed = factor * confirmed,
    unconfirmed = factor * unconfirmed,
    autoExchangeConfirmed = factor * autoExchangeConfirmed,
    autoExchangeUnconfirmed = factor * autoExchangeUnconfirmed,
    onExchange = factor * onExchange
)
