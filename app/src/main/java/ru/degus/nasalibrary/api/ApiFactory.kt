package ru.degus.nasalibrary.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//класс фабрика для создания Retrofit
class ApiFactory(private val nasaUrl: String, private val client: OkHttpClient) {

    private inline fun <reified T> getApi(baseUrl: String): T { //создание Retrofit cо стандартными параметрами
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }

    fun getINasaApi(): INasaApi = getApi(nasaUrl) // метод возвращающий Api с url "https://images-api.nasa.gov/"

}