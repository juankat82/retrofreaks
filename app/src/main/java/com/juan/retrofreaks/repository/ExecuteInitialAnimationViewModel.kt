package com.juan.retrofreaks.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val IS_INITIAL_ANIMATION_EXECUTING = "is_initial_animation_executing"

/*
This class is used to determine if the Intro letters are set to spin or not.
Generaly this should be NOT spinning if the app didnt invoke its onDestroy() and it will
write on SharedPreferences if it invoked such method or not.
 */
class ExecuteInitialAnimationViewModel(context: Context) : ViewModel() {

    private val myPreferencesRepository = PreferencesRepository.getSharedPreferences(context)
    private val mySharedPreferencesEditor = PreferencesRepository.getSharedPreferencesEditor()

    private val _isInitialAnimationExecuting : MutableLiveData<Boolean> = MutableLiveData(myPreferencesRepository.getBoolean(
        IS_INITIAL_ANIMATION_EXECUTING,true))

    val isInitialAnimationExecuting : LiveData<Boolean>
        get() = _isInitialAnimationExecuting

    fun setIsInitialAnimationExecuting(isExecuting: Boolean) {
        mySharedPreferencesEditor.putBoolean(IS_INITIAL_ANIMATION_EXECUTING,isExecuting)
        _isInitialAnimationExecuting.postValue(isExecuting)
    }
}