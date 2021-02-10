package ru.degus.nasalibrary.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.di.modules.ActivityModule
import ru.degus.nasalibrary.di.modules.ApiFactoryModule
import ru.degus.nasalibrary.di.modules.NasaRepoModule
import ru.degus.nasalibrary.fragments.AudioFragment
import ru.degus.nasalibrary.fragments.ImageFragment
import ru.degus.nasalibrary.fragments.MainFragment
import ru.degus.nasalibrary.fragments.VideoFragment
import ru.degus.nasalibrary.viewmodels.AudioViewModel
import ru.degus.nasalibrary.viewmodels.ImageViewModel
import ru.degus.nasalibrary.viewmodels.MainViewModel
import ru.degus.nasalibrary.viewmodels.VideoViewModel
import javax.inject.Singleton

@Component(
    modules = [
        ApiFactoryModule::class,
        NasaRepoModule::class
    ]
)
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance
        fun application(appModule: App): Builder
    }

    fun viewModelSubComponentBuilder(): ViewModelSubComponent.Builder                   //методы создания sub компонентов
    fun activitySubComponentBuilder(): ActivitySubComponent.Builder
}

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun inject(vModel: MainViewModel)                                                   //методы инъекции зависимостей во viewModel
    fun inject(vModel: ImageViewModel)
    fun inject(vModel: VideoViewModel)
    fun inject(vModel: AudioViewModel)

}

@Subcomponent(modules = [ActivityModule::class])
interface ActivitySubComponent {
    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun with(activity: AppCompatActivity): Builder

        fun build(): ActivitySubComponent
    }

    fun inject(mainFragment: MainFragment)                                              //методы инъекции зависимостей во фрагменты
    fun inject(imageFragment: ImageFragment)
    fun inject(videoFragment: VideoFragment)
    fun inject(audioFragment: AudioFragment)
}