package com.juan.retrofreaks.activities

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.barteksc.pdfviewer.PDFView
import com.juan.retrofreaks.MainActivity
import com.juan.retrofreaks.repository.RouteViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MiscDestinationUITest {

    /**
     * Only for composables that dont depend on an Activity
     */
    @get:Rule
    val mainComposable = createComposeRule()

    /**
     * For composables that DO depend on an activity
     */
    @get:Rule
    val main = createAndroidComposeRule<MainActivity>()

    /**
     * uses lifecycle of the activity. Allows using onActivity{} and LifecycleState.STARTED, RESUMED, etc
     * and also running coroutines.
     *  If you want to test recreation of Activity instance, use recreate().
     *  Check: https://developer.android.com/reference/androidx/test/core/app/ActivityScenario
     */
    @get:Rule
    val scenarioRule = activityScenarioRule<MainActivity>()


    /**
     * To avoid flaky tests, check this out:
     * https://proandroiddev.com/managing-flaky-tests-in-jetpack-compose-89c598590068
     */

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val navController = NavHostController(context)
    val currentLocale = "en_GB"
    val routeViewmodel = RouteViewModel(context)
    var pdfView: PDFView? = null

    @Before
    fun init() {
        pdfView = main.activity.pdfView
    }

    @Test
    fun test_flashcart_view_is_launched() {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "flashcarts"
            FlashcartView(navController, consoleId, currentLocale, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("pdf_view").assertIsDisplayed()
    }

    @Test
    fun test_upscalers_view_is_launched() {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "upscalers"
            UpscalersView(navController, consoleId, currentLocale, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("pdf_view").assertIsDisplayed()
    }

    @Test
    fun test_where_to_buy_view_is_launched() {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "where_to_buy"
            WhereToBuyView(navController, consoleId, currentLocale, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("pdf_view").assertIsDisplayed()
    }

    @Test
    fun test_links_view_is_launched() {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "links"
            LinksView(navController, consoleId, currentLocale, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("pdf_view").assertIsDisplayed()
    }

    @Test
    fun test_about_view_is_launched() {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "about"
            AboutView(navController, consoleId, currentLocale, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("pdf_view").assertIsDisplayed()
    }
    ///////////END OF MISC VIEWS//////////////////////
    @Test
    fun test_get_file_name() {
        assert(getFileName("flashcarts", "es_ES").equals("flashcarts_es.pdf"))
    }
}