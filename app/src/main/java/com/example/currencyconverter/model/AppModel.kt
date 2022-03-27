package com.example.currencyconverter.model

import com.example.currencyconverter.activity.Constant
import com.example.currencyconverter.main.DefaultMainRepository
import com.example.currencyconverter.service.CurrencyService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModel {


   //provide retrofit instance when it need
    @Singleton
    @Provides
    fun provideCurrencyService(): CurrencyService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(CurrencyService::class.java)
    }

    //provide repository with retrofit instance
    @Singleton
    @Provides
    fun provideMainRepository(service:CurrencyService) = DefaultMainRepository(service)


}