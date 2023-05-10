package com.example.di

import com.example.data.DataRepository
import com.example.data.IDataRepository
import com.example.data.DataService
import org.koin.dsl.module

val appModule = module {
    single<IDataRepository> { DataRepository() }
    single { DataService(get()) }
}