package com.example.currencyconverter.main

import com.example.currencyconverter.model.CurrencyResponse
import com.example.currencyconverter.util.Resource

//interface for default main repository
interface MainRepository {

    //use coroutine to get response from api and pass the response to the resource
    suspend fun getConvertedCurrency(access_key:String,from:String,to:String,amount:Double):Resource<CurrencyResponse>


}