package com.example.currencyconverter.model

import java.io.Serializable


data class CurrencyResponse (
      val base_currency_code:String,
      val base_currency_name:String,
      val amount:String,
      val updated_date:String,
      val rates:HashMap<String,Rates> = HashMap(),
      val status:String,
      ):Serializable