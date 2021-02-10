package ru.degus.nasalibrary.di.modules

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.degus.nasalibrary.viewmodels.AudioViewModel
import ru.degus.nasalibrary.viewmodels.ImageViewModel
import ru.degus.nasalibrary.viewmodels.MainViewModel
import ru.degus.nasalibrary.viewmodels.VideoViewModel

@Module
class ActivityModule {

    @Provides       //Метод предоставляющий MainViewModel
    fun getMainViewModel(activity: AppCompatActivity): MainViewModel {
        return ViewModelProvider(activity).get(MainViewModel::class.java)
    }

    @Provides      //Метод предоставляющий ImageViewModel
    fun getImageViewModel(activity: AppCompatActivity): ImageViewModel {
        return ViewModelProvider(activity).get(ImageViewModel::class.java)
    }

    @Provides      //Метод предоставляющий VideoViewModel
    fun getVideoViewModel(activity: AppCompatActivity): VideoViewModel {
        return ViewModelProvider(activity).get(VideoViewModel::class.java)
    }

    @Provides      //Метод предоставляющий AudioViewModel
    fun getAudioViewModel(activity: AppCompatActivity): AudioViewModel {
        return ViewModelProvider(activity).get(AudioViewModel::class.java)
    }
}