package com.juan.retrofreaks.activities

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenUITest {

    @get:Rule
    val testRule: ComposeContentTestRule = createComposeRule()

    @Test
    fun test_constant_destination_route_names() {
        testRule.setContent {
            assert(BASE_SCREEN.equals("base_screen"))
            assert(CONSOLE_SCREEN.equals("console_screen"))
            assert(ACTION_NAME_ARG.equals("actionName"))
            assert(MISC_SCREEN.equals("misc_screen"))
            assert(MISC_EMPTY.equals("misc_empty"))
            assert(FLASHCARTS_ODE.equals( "flashcarts"))
        }
    }

    @Test
    fun test_screen_returned_routes() {
        assert(Screen.Home.getBaseScreen().equals(BASE_SCREEN))
        assert(Screen.Console.getBaseScreen().equals(CONSOLE_SCREEN))
        assert(Screen.Misc.getBaseScreen().equals(MISC_SCREEN))
    }
}