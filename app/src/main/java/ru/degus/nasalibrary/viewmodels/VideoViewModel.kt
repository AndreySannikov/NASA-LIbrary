package ru.degus.nasalibrary.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.degus.nasalibrary.models.SearchInfo
import ru.degus.nasalibrary.repository.INasaRepo
import ru.degus.nasalibrary.util.VIDEO
import ru.degus.nasalibrary.util.videoLink
import javax.inject.Inject

class VideoViewModel : AbstractViewModel() {
    @Inject
    lateinit var nasaRepo: INasaRepo

    val videoUrlData: MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val videoData: MutableLiveData<SearchInfo> by lazy { MutableLiveData<SearchInfo>() }
    val errorData: MutableLiveData<Throwable> by lazy { MutableLiveData<Throwable>() }

    fun downloadVideoUrl(videoUrl: String?) {                                   //загрузка списка данных об аудио
        addDisposable(
            videoUrl?.let { link ->
                nasaRepo.downloadVideoData(link.videoLink())                    //обрезка строки для игнорирования базового URL
                    .subscribe({
                        Log.d("LoadVideoData", it.toString())
                        videoUrlData.value = it                                 //установка списка данных в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSVideoData", it.toString())
                    })
            }
        )
    }

    fun downloadVideo(id: String?) {                                             //загрузка видео объекта по его id
        addDisposable(
            id?.let { id ->
                nasaRepo.downloadSearchData(id, VIDEO)
                    .subscribe({
                        Log.d("LoadSearchData", it.toString())
                        videoData.value = it                                       //установка видео объекта в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSearchData", it.toString())
                    })
            }
        )
    }
}