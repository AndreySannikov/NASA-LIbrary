package ru.degus.nasalibrary.di.modules

import dagger.Module
import dagger.Provides
import ru.degus.nasalibrary.api.ApiFactory
import ru.degus.nasalibrary.repository.INasaRepo
import ru.degus.nasalibrary.repository.NasaRepo
import javax.inject.Singleton

@Module(includes = [ApiFactoryModule::class])
class NasaRepoModule {

    @Singleton //метод предоставляющий NasaRepo
    @Provides
    fun getAlbumRepo(apiFactory: ApiFactory): INasaRepo {
        return NasaRepo(apiFactory)
    }

}