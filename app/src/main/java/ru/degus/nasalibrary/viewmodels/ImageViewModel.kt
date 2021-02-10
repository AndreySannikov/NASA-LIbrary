package ru.degus.nasalibrary.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.degus.nasalibrary.models.SearchInfo
import ru.degus.nasalibrary.repository.INasaRepo
import ru.degus.nasalibrary.util.IMAGE
import javax.inject.Inject

class ImageViewModel : AbstractViewModel() {
    @Inject
    lateinit var nasaRepo: INasaRepo

    val imageData: MutableLiveData<SearchInfo> by lazy { MutableLiveData<SearchInfo>() }
    val errorData: MutableLiveData<Throwable> by lazy { MutableLiveData<Throwable>() }

    fun downloadImage(id: String?) {                                   //загрузка объекта изображения по его id
        addDisposable(
            id?.let { id ->
                nasaRepo.downloadSearchData(id, IMAGE)
                    .subscribe({
                        Log.d("LoadSearchData", it.toString())
                        imageData.value = it                            //установка объекта изображения в значение LiveData
                    }, {
                        errorData.value = it
                        Log.d("LoadSearchData", it.toString())
                    })
            }
        )
    }
}