package com.juan.retrofreaks.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juan.retrofreaks.activities.Screen

const val ROUTE = "base_route"
const val OLD_ROUTE = "old_route"

/*
Handles a ViewModel+LiveData that are in charge of taking/putting in the current route.
This is useful for change of configuration.
 */
class RouteViewModel(context: Context) : ViewModel() {
    private val myPreferencesRepository = PreferencesRepository.getSharedPreferences(context)
    private val mySharedPreferencesEditor = PreferencesRepository.getSharedPreferencesEditor()


    private val _route:MutableLiveData<String> = MutableLiveData(
        myPreferencesRepository.getString(ROUTE, Screen.Console.getBaseScreen())
    )
    val route:LiveData<String>
    get() = _route

    fun setRoute(route:String) {
        _route.value = route
        mySharedPreferencesEditor.putString(ROUTE,route).apply()
    }

    private val _oldRoute:MutableLiveData<String> = MutableLiveData(
        myPreferencesRepository.getString(OLD_ROUTE, "")
    )
    val oldRoute:LiveData<String>
        get() = _oldRoute

    fun setOldRoute(route:String) {
        _oldRoute.value = route
        mySharedPreferencesEditor.putString(OLD_ROUTE,route).apply()
    }
}