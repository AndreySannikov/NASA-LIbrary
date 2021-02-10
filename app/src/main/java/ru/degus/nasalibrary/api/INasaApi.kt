package ru.degus.nasalibrary.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.degus.nasalibrary.models.Collection
import ru.degus.nasalibrary.models.SearchInfo

interface INasaApi {
    @GET("search")
    fun getSearchData(@Query("q")name: String, @Query("media_type")type: String): Observable<SearchInfo> // get запрос на сервер ищущий данные по строке name с учетом media_type

    @GET("https://images-assets.nasa.gov/video/{link}")                   //ссылка указанна полностью для игнорирования базового URL
    fun getVideoData(@Path("link")name: String): Observable<List<String>> // get запрос на сервер для получения информации о видео

    @GET("https://images-assets.nasa.gov/audio/{link}")
    fun getAudioData(@Path("link")name: String): Observable<List<String>> // get запрос на сервер для получения информации об аудио
}