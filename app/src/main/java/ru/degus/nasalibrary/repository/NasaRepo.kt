package ru.degus.nasalibrary.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.degus.nasalibrary.api.ApiFactory
import ru.degus.nasalibrary.models.SearchInfo

class NasaRepo(private val apiFactory: ApiFactory) : INasaRepo {

    override fun downloadSearchData(name: String, type: String): Observable<SearchInfo> {       //ассинхронное получение списка данных по строке
        return apiFactory.getINasaApi().getSearchData(name, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun downloadVideoData(name: String): Observable<List<String>> {            //ассинхронное получение списка данных о видео
        return apiFactory.getINasaApi().getVideoData(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun downloadAudioData(name: String): Observable<List<String>> {            //ассинхронное получение списка данных об аудио
        return apiFactory.getINasaApi().getAudioData(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}