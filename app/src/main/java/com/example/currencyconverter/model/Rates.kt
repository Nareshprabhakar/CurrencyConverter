package com.example.currencyconverter.model

import java.io.Serializable

data class Rates (
    val currency_name:String,
    val rate:String,
    val rate_for_amount:Double
        ):Serializable
