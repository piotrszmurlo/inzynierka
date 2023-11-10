package com.inzynierka.di

import com.inzynierka.data.repository.DataRepository
import com.inzynierka.data.repository.IDataRepository
import com.inzynierka.data.service.DataService
import com.inzynierka.domain.service.IDataService
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val API_URL = "http://127.0.0.1:8000/"

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
                install(DefaultRequest) {
                    url(API_URL)
                }
                expectSuccess = true
            }
        )
    }
    single<IDataService> { DataService(get()) }
}