package com.juan.retrofreaks.activities

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.retrofreaks.repository.RouteViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaseDestinationUITest {

    @get:Rule
    val testRule: ComposeContentTestRule = createComposeRule()

    val context = InstrumentationRegistry.getInstrumentation().context
    val navController = NavHostController(context)

    @Test
    fun test_base_destination_sets_base_route() {
        val routeViewmodel = RouteViewModel(navController.context)
        testRule.setContent {
            BaseDestination(navController)
            routeViewmodel.setRoute(BASE_SCREEN)
        }
        Log.i("BASE_SCREEN_ROUTE",routeViewmodel.route.value.toString())
    }

    @Test
    fun test_empty_destination_is_called_and_has_composable_items() {
        testRule.setContent {
            BaseDestination(navController)
        }
        testRule.onNodeWithContentDescription("emptyImage").assertIsDisplayed()
    }
}