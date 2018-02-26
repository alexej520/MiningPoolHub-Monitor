package ru.lextop.miningpoolhub.vo

enum class Status { SUCCESS, ERROR, LOADING }

infix fun Status.and(other: Status): Status =
    when (this) {
        Status.SUCCESS -> other
        Status.LOADING -> {
            when (other) {
                Status.ERROR -> Status.ERROR
                else -> Status.LOADING
            }
        }
        else -> Status.ERROR
    }
