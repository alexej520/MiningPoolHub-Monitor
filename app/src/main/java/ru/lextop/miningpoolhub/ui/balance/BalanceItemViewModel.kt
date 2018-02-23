package ru.lextop.miningpoolhub.ui.balance

import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.Currency

data class BalanceItemViewModel(
    val id: String,
    val currency: Currency?,
    val balance: Balance?
) {
    companion object {
        @JvmStatic
        fun formatBalance(balance: Double): String {
            if (balance.isNaN()) return ""
            return "%f".format(balance)
        }
    }
}
