package ru.lextop.miningpoolhub.ui.balance

object BalanceItemViewModel {
    @JvmStatic
    fun formatBalance(balance: Double): String {
        if (balance.isNaN()) return ""
        return "%f".format(balance)
    }
}
