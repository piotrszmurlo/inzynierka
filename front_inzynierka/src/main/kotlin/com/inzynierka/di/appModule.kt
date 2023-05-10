package com.inzynierka.di

import com.inzynierka.data.DataRepository
import com.inzynierka.data.IDataRepository
import com.inzynierka.data.DataService
import org.koin.dsl.module

val appModule = module {
    single<IDataRepository> { DataRepository() }
    single { DataService(get()) }
}