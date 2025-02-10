package com.juan.retrofreaks.activities

const val BASE_SCREEN = "base_screen"
const val CONSOLE_SCREEN = "console_screen"
const val ACTION_NAME_ARG = "actionName"
const val MISC_SCREEN = "misc_screen"
const val MISC_EMPTY = "misc_empty"
const val FLASHCARTS_ODE = "flashcarts"

/*
Implements route names for NavGraph capabilities
 */
sealed class Screen {

    object Home : Screen (){
        fun getBaseScreen() : String {
            return BASE_SCREEN
        }
    }

    object Console : Screen (){
        fun getBaseScreen() : String {
            return CONSOLE_SCREEN
        }
    }

    object Misc : Screen () {
        fun getBaseScreen() : String {
            return MISC_SCREEN
        }
    }
}
