package ru.degus.nasalibrary.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.degus.nasalibrary.models.SearchInfo
import ru.degus.nasalibrary.repository.INasaRepo
import ru.degus.nasalibrary.util.AUDIO
import ru.degus.nasalibrary.util.audioLink
import javax.inject.Inject

class AudioViewModel : AbstractViewModel() {
    @Inject
    lateinit var nasaRepo: INasaRepo

    val audioUrlData: MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val audioData: MutableLiveData<SearchInfo> by lazy { MutableLiveData<SearchInfo>() }
    val errorData: MutableLiveData<Throwable> by lazy { MutableLiveData<Throwable>() }

    fun downloadAudioUrl(audioUrl: String?) {                                   //загрузка списка данных об аудио
        addDisposable(
            audioUrl?.let { link ->
                nasaRepo.downloadAudioData(link.audioLink())                    //обрезка строки для игнорирования базового URL
                    .subscribe({
                        Log.d("LoadVideoData", it.toString())
                        audioUrlData.value = it                                       //установка списка данных в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSVideoData", it.toString())
                    })
            }
        )
    }

    fun downloadAudio(id: String?) {                                   //загрузка аудио объекта по его id
        addDisposable(
            id?.let { id ->
                nasaRepo.downloadSearchData(id, AUDIO)
                    .subscribe({
                        Log.d("LoadSearchData", it.toString())
                        audioData.value = it                                       //установка аудио объекта в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSearchData", it.toString())
                    })
            }
        )
    }
}