package ru.degus.nasalibrary

import android.app.Application
import ru.degus.nasalibrary.di.AppComponent
import ru.degus.nasalibrary.di.DaggerAppComponent
import ru.degus.nasalibrary.di.ViewModelSubComponent

class App : Application() {

    var name: String? = null
        get() = resources.getString(R.string.app_name)
        private set

    private lateinit var appComponent: AppComponent
    private lateinit var viewModelSubComponent: ViewModelSubComponent

    fun getAppComponent(): AppComponent {
        return appComponent
    }

    fun getViewModelSubComponent(): ViewModelSubComponent {
        return viewModelSubComponent
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        viewModelSubComponent = appComponent.viewModelSubComponentBuilder().build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}