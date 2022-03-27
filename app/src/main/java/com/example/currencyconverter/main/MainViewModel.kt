package com.example.currencyconverter.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.model.Rates
import com.example.currencyconverter.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (private val repo:DefaultMainRepository):ViewModel()  {

   //for event handling
    sealed class CurrencyConversionEvent{
        class Success(val data:Any):CurrencyConversionEvent()
        class Error(val message:String):CurrencyConversionEvent()
        object Loading:CurrencyConversionEvent()
        object Empty:CurrencyConversionEvent()
    }

   //mutable state flow for observe the event
    private val _conversion = MutableStateFlow<CurrencyConversionEvent>(CurrencyConversionEvent.Empty)

    //emit the event in main activity
    val conversion:StateFlow<CurrencyConversionEvent> = _conversion

    fun getConvertedCurrency(access_key:String,from:String,to:String,amount:Double){

        viewModelScope.launch(Dispatchers.IO) {
            _conversion.value = CurrencyConversionEvent.Loading

            when(val currencyResponse = repo.getConvertedCurrency(access_key,from,to,amount)){

                is Resource.Error -> _conversion.value = CurrencyConversionEvent.Error(currencyResponse.message!!)


                is Resource.Success -> {

                    val rates = currencyResponse.data!!.rates
                    val map:Map<String, Rates>
                    map = rates
                    map.keys.forEach {
                        val rateForAmount = map[it]?.rate_for_amount
                        val formattedAmount = String.format("%,.2f",rateForAmount)
                        _conversion.value = CurrencyConversionEvent.Success(formattedAmount)
                    }

                }

            }
        }


    }






}