package ru.degus.nasalibrary.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.degus.nasalibrary.models.SearchInfo
import ru.degus.nasalibrary.repository.INasaRepo
import ru.degus.nasalibrary.util.AUDIO
import ru.degus.nasalibrary.util.FIRST_REQUEST
import ru.degus.nasalibrary.util.IMAGE
import ru.degus.nasalibrary.util.VIDEO
import javax.inject.Inject

class MainViewModel : AbstractViewModel() {
    @Inject
    lateinit var nasaRepo: INasaRepo

    var isImage = true                         //переменные сохраняющие положения чекбоксов
    var isVideo = true                         //отвечающих за выбор типов объектов для поиска
    var isAudio = true

    var searchString = FIRST_REQUEST            //поисковая строка с установленным начальным запросом

    val searchData: MutableLiveData<SearchInfo> by lazy { MutableLiveData<SearchInfo>() }
    val errorData: MutableLiveData<Throwable> by lazy { MutableLiveData<Throwable>() }


    fun search() {                                   //загрузка списка элементов по поисковой строке с учетом выбранных типов
        if (searchString.isNotEmpty())
            addDisposable(
                nasaRepo.downloadSearchData(searchString, filter())
                    .subscribe({
                        Log.d("LoadSearchData", it.toString())
                        searchData.value = it                                       //установка списка элементов в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSearchData", it.toString())
                    })
            )
    }

    private fun filter() : String {                                         //метод для составление значения media_type для поиска
        val types : MutableList<String> = mutableListOf()
        if (isImage) types.add(IMAGE)
        if (isVideo) types.add(VIDEO)
        if (isAudio) types.add(AUDIO)
        return types.joinToString(",")
    }
}