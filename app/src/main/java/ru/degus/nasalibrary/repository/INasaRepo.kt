package ru.degus.nasalibrary.repository

import io.reactivex.Observable
import ru.degus.nasalibrary.models.SearchInfo

interface INasaRepo {
    fun downloadSearchData(name: String, type: String): Observable<SearchInfo>  //загрузка списка данных по строке name с учетом типов
    fun downloadVideoData(name: String): Observable<List<String>>  //загрузка списка данных о видео
    fun downloadAudioData(name: String): Observable<List<String>>  //загрузка списка данных об аудио
}