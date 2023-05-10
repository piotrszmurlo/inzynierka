package com.inzynierka.di

import com.inzynierka.data.DataRepository
import com.inzynierka.data.DataService
import com.inzynierka.data.IDataRepository
import com.inzynierka.domain.service.IDataService
import io.kvision.rest.RestClient
import org.koin.dsl.module

val appModule = module {
    single<IDataRepository> { DataRepository(RestClient()) }
    single<IDataService> { DataService(get()) }
}