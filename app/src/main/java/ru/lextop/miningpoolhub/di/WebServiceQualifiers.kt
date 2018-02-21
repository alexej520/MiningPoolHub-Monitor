package ru.lextop.miningpoolhub.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Coinmarketcap(val name: String = "")

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Miningpoolhub(val name: String = "")
