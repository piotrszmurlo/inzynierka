package com.inzynierka.di

import com.inzynierka.data.DataRepository
import com.inzynierka.data.DataService
import com.inzynierka.data.IDataRepository
import com.inzynierka.domain.service.IDataService
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single<IDataRepository> {
        DataRepository(
            HttpClient(Js) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                        }
                    )
                }
                expectSuccess = true
            }
        )
    }
    single<IDataService> { DataService(get()) }
}