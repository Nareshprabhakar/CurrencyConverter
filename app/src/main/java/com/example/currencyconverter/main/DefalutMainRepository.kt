package com.example.currencyconverter.main

import com.example.currencyconverter.model.CurrencyResponse
import com.example.currencyconverter.service.CurrencyService
import com.example.currencyconverter.util.Resource
import java.io.IOException
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(private val service:CurrencyService):MainRepository {

    override suspend fun getConvertedCurrency(
        access_key: String,
        from: String,
        to: String,
        amount: Double): Resource<CurrencyResponse> {
        return try {
            //get response from the api
            val response = service.getConvertedCurrency(access_key,from,to,amount)
            val result = response.body()

            //response success
            if(response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                 //response failure
                Resource.Error(response.message())
            }
        }catch (e:IOException){
            Resource.Error(e.message!!)
        }
    }
}

