package com.example.currencyconverter.activity


import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null

    private  var mCountryName1:String? = null
    private  var mCountryName2:String? = null

    private var selectedItem1:String = "INR"
    private var selectedItem2:String = "INR"

    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.white_)

       //initialize different font
        val typeFace:Typeface = Typeface.createFromAsset(assets,"good times rg.otf")
        binding?.tvTitleLine1?.typeface = typeFace
        binding?.tvTitleLine2?.typeface = typeFace
        

        //activate both spinners
        activateSpinners()

       //for onClick event
        onClickListener()

        binding?.ivSwap?.setOnClickListener {

            when{
                mCountryName1.isNullOrEmpty()->{
                    Toast.makeText(this,"please choose from country",Toast.LENGTH_LONG).show()
                }
                mCountryName2.isNullOrEmpty()->{
                    Toast.makeText(this,"please choose to country",Toast.LENGTH_LONG).show()
                }
                else ->{
                    swapCountry()
                }
            }
        }
    }



    // for getting Country List
    private fun getAllCountries():ArrayList<String>{
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for(i in locales){
            val country = i.displayCountry
            if( !countries.contains(country) && country.trim().isNotEmpty() ) {
                countries.add(country)
            }
        }
       countries.sort()
        return countries
    }
   // getting Country Code from Country Name
    private fun getCountryCode(countryName:String) = Locale.getISOCountries().
                                                     find {Locale("",it).displayCountry == countryName}

    // getting CurrencySymbol from CountryCode
    private fun getCurrencySymbol(countryCode:String?):String?{
        val locales = Locale.getAvailableLocales()
        for(i in locales.indices){
           if(locales[i].country == countryCode)
           return Currency.getInstance(locales[i]).currencyCode
        }
        return ""
    }

    private fun activateSpinners(){

        // for spinner1
        val spinner1 = binding?.msFromCountry

        spinner1?.setItems(getAllCountries())

        spinner1?.setOnClickListener {
            Constant.hideKeyBoard(this)
        }


        spinner1?.setOnItemSelectedListener { _, _, _, item ->
            mCountryName1 = item.toString()
            val countryCode = getCountryCode(mCountryName1!!)
            val currencySymbol = getCurrencySymbol(countryCode)
            selectedItem1 = currencySymbol!!
            binding?.tvFromCurrencyUnit?.visibility = View.VISIBLE
            binding?.tvFromCurrencyUnit?.text = selectedItem1
        }


        //for spinner2
        val spinner2 = binding?.msToCountry

        spinner2?.setItems(getAllCountries())

        spinner2?.setOnClickListener {
            Constant.hideKeyBoard(this)
        }


        spinner2?.setOnItemSelectedListener { _, _, _, item ->
            mCountryName2 = item.toString()
            val countryCode = getCountryCode(mCountryName2!!)
            val currencySymbol = getCurrencySymbol(countryCode)
            selectedItem2 = currencySymbol!!
            binding?.tvToUnit?.visibility = View.VISIBLE
            binding?.tvToUnit?.text = selectedItem2
        }

    }

    private fun onClickListener() {

        binding?.btnConvert?.setOnClickListener {
            val amountToConvert = binding?.etAmount?.text.toString()

            if(amountToConvert.isEmpty() || amountToConvert == "0" ){
                Toast.makeText(this,"Please Enter  Some Amount For Conversion",Toast.LENGTH_LONG).show()
            }

            //check network connection
            else if(!Constant.isNetworkAvailable(this)){
                Constant.hideKeyBoard(this)
                Toast.makeText(this,"Please Check Your Network Connection",Toast.LENGTH_LONG).show()
            }

            else {
                val amountToDouble = amountToConvert.toDouble()

               //initialize conversion
                viewModel.getConvertedCurrency(
                    Constant.API_KEY,
                    selectedItem1,
                    selectedItem2,
                    amountToDouble
                )

                // for update ui
                setupUi()
            }



        }

    }

    private fun setupUi() {

        Constant.hideKeyBoard(this)

       // use coroutine to observe events and response from api

        lifecycleScope.launchWhenStarted {

            viewModel.conversion.collect { event->
                when(event){

                    is MainViewModel.CurrencyConversionEvent.Success ->{

                        binding?.etResult?.setText(event.data.toString())

                        binding?.progressBar?.visibility = View.GONE

                        binding?.btnConvert?.visibility = View.VISIBLE

                    }

                    is MainViewModel.CurrencyConversionEvent.Error ->{
                        val layOut = binding?.mainLayout
                        if (layOut != null) {
                            Snackbar.make(layOut, event.message,Snackbar.LENGTH_LONG).show()
                        }

                    }

                    is MainViewModel.CurrencyConversionEvent.Loading ->{
                        binding?.progressBar?.visibility = View.VISIBLE
                        binding?.btnConvert?.visibility = View.INVISIBLE

                    }

                    else -> Unit

                }

            }
        }
    }

    private fun swapCountry(){

        mCountryName1 = mCountryName2.also {
            mCountryName2 = mCountryName1
        }

        binding?.msFromCountry?.text = mCountryName1
        binding?.msToCountry?.text = mCountryName2

        //for From Country
        val countryCode1 = getCountryCode(mCountryName1!!)
        val currencySymbol1= getCurrencySymbol(countryCode1)
        selectedItem1 = currencySymbol1!!
        binding?.tvFromCurrencyUnit?.text = selectedItem1

        //for To Country
        val countryCode2 = getCountryCode(mCountryName2!!)
        val currencySymbol2 = getCurrencySymbol(countryCode2)
        selectedItem2 = currencySymbol2!!
        binding?.tvToUnit?.text = selectedItem2

        }

}
