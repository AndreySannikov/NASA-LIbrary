package ru.degus.nasalibrary.di.modules

import android.util.Log
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.degus.nasalibrary.api.ApiFactory
import ru.degus.nasalibrary.util.NASA_API_URL
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiFactoryModule {

    @Singleton              //метод предоставляющий Api с базовым url
    @Provides
    fun apiFactory(@Named("nasaUrl") nasaUrl: String, @Named("okhhtp_logging")client: OkHttpClient): ApiFactory {
        return ApiFactory(nasaUrl, client)
    }

    @Provides
    @Named("nasaUrl")      //метод предоставляющий базовый url
    fun nasaUrl() = NASA_API_URL

    @Provides
    @Named("okhhtp_logging")
    fun getOkHttpClient(): OkHttpClient {
        val c = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("OkHttpLogger", it)
        })

        logging.level = HttpLoggingInterceptor.Level.BASIC
        c.addInterceptor(logging)
        return c.build()
    }
}