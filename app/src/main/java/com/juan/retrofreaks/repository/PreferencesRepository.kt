package com.juan.retrofreaks.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

const val SHARED_PREFERENCES_ID = "shared_preferences_id"
const val SHARED_PREFERENCES_LANGUAGE = "shared_preferences_language"
const val FILE_NAME_PREFERENCES = "file_name"


lateinit var sharedPreferences:SharedPreferences
lateinit var sharedPreferencesEditor: SharedPreferences.Editor

/*
Shortcut extension functions for reading/write the SharedPreferences
 */
class PreferencesRepository {

    companion object {
        fun getSharedPreferences(context: Context): SharedPreferences {
                sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_ID, MODE_PRIVATE)
            return sharedPreferences
        }

        fun getSharedPreferencesEditor(): SharedPreferences.Editor {
            sharedPreferencesEditor = sharedPreferences.edit()
            return sharedPreferencesEditor
        }
    }
}