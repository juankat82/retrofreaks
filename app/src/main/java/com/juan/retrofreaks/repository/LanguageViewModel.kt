package com.juan.retrofreaks.repository

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

/*
This class uses a ViewModel+LiveData that registers what locale is in use and writes it
in SharedPreferences.
 */
class LanguageViewModel (context: Context) : ViewModel() {

    private val myPreferencesRepository = PreferencesRepository.getSharedPreferences(context)
    private val mySharedPreferencesEditor = PreferencesRepository.getSharedPreferencesEditor()

    private val _locale : MutableLiveData<String> = MutableLiveData(
        myPreferencesRepository.getString(SHARED_PREFERENCES_LANGUAGE,"${Locale.UK}")
    )

    val locale :LiveData<String>
    get() = _locale

    //Sets locale
    fun setLocale(locale: String, context:Context) {
        val newLocale = Locale(locale.split("_")[0])
        Locale.setDefault(newLocale)
        val res = (context as Activity).resources
        val conf = res.configuration
        conf.setLocale(newLocale)
        conf.setLayoutDirection(newLocale)
        res.updateConfiguration(conf,res.displayMetrics)
        mySharedPreferencesEditor.putString(SHARED_PREFERENCES_LANGUAGE,locale).commit()
        _locale.value = locale
    }
}