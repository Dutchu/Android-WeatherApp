package lol.dutchu.myweather.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lol.dutchu.myweather.data.repository.LocationRepositoryImpl
import javax.inject.Singleton
import lol.dutchu.myweather.data.repository.WeatherRepositoryImpl
import lol.dutchu.myweather.domain.repository.LocationRepository
import lol.dutchu.myweather.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}