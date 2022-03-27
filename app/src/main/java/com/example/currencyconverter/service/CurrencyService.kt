package com.example.currencyconverter.service

import com.example.currencyconverter.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface CurrencyService {

@GET("convert")
suspend fun getConvertedCurrency(
    @Query("api_key") access_key:String,
    @Query("from") from:String,
    @Query("to") to:String,
    @Query("amount") amount: Double
):Response<CurrencyResponse>

}