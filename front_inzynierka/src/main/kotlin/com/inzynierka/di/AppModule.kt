package com.inzynierka.di

import com.inzynierka.data.repository.*
import com.inzynierka.data.service.BenchmarkService
import com.inzynierka.data.service.FileService
import com.inzynierka.data.service.RankingsService
import com.inzynierka.data.service.UserService
import com.inzynierka.domain.service.IBenchmarkService
import com.inzynierka.domain.service.IFileService
import com.inzynierka.domain.service.IRankingsService
import com.inzynierka.domain.service.IUserService
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val API_URL = "http://127.0.0.1:8000/"

val client = HttpClient(Js) {
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

val appModule = module {
    single<IBenchmarkRepository> {
        BenchmarkRepository(client)
    }
    single<IBenchmarkService> { BenchmarkService(get()) }
    single<IRankingsRepository> {
        RankingsRepository(client)
    }
    single<IRankingsService> { RankingsService(get()) }
    single<IUserRepository> {
        UserRepository(client)
    }
    single<IUserService> { UserService(get()) }
    single<IFileRepository> {
        FileRepository(client)
    }
    single<IFileService> { FileService(get()) }
}
