package com.juan.retrofreaks.activities

import android.content.Context
import android.webkit.WebView
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.barteksc.pdfviewer.PDFView
import com.juan.retrofreaks.MainActivity
import com.juan.retrofreaks.repository.RouteViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import java.util.*

class ConsoleDestinationUiTest {

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

    @get:Rule
    val scenarioRule = activityScenarioRule<MainActivity>()

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val navController = NavHostController(context)
    val currentLocale = "en_GB" // "es_ES"
    val routeViewmodel = RouteViewModel(navController.context)
    var pdfView:PDFView? = null
    var webView:WebView? = null

    @Before
    fun init() {
        val pdfViewWrapper = main.activity.getMyPDFViewWrapper()
        val webViewWrapper = main.activity.getMyWebViewWrapper()
        pdfView = pdfViewWrapper.getWPDFView()
        webView = webViewWrapper.getWebView()
    }


    ////////////////TEST CONSOLE VIEWS EXISTS//////////////////
    @Test
    fun test_base_destination_is_launched () {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "atari_2600_en.pdf"
            BaseConsoleView(navController, currentLocale, consoleId, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("base_console_layout").assertIsDisplayed()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun test_games_destination_is_launched () {
        mainComposable.setContent {
           AndroidView(factory = { context ->
               WebView(context, null)
           })
            val consoleId = "atari_2600"
            GamesView(consoleId, main.activity.context)
        }
        mainComposable.onNodeWithContentDescription("webview_layout").assertIsDisplayed()
    }

    @Test
    fun test_accesories_destination_is_launched () {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "atari_2600_accessories_es.pdf"
            AccesoriesView(navController.context,currentLocale,consoleId.lowercase(), pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("accesories_view_layout").assertIsDisplayed()
    }

    @Test
    fun test_cables_destination_is_launched () {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "atari_2600_cables_en.pdf"
            CablesView(navController, currentLocale, consoleId, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("cables_view_layout").assertIsDisplayed()
    }

    @Test
    fun test_mods_destination_is_launched () {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                PDFView(context, null)
            })
            val consoleId = "atari_2600_mods_en.pdf"
            ModsView(navController, currentLocale, consoleId, pdfView!!)
        }
        mainComposable.onNodeWithContentDescription("mods_view_layout").assertIsDisplayed()
    }

    @Test
    fun test_error_destination_is_launched () {
        mainComposable.setContent {
            ErrorView()
        }
        mainComposable.onNodeWithContentDescription("error_view_layout").assertIsDisplayed()
    }
    ////////////////ENDS TEST CONSOLE VIEWS EXISTS//////////////////
    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun test_games_search_is_working () {
        mainComposable.setContent { // }Composable.setContent {
            AndroidView(factory = { context ->
                WebView(context, null)
            })
            val consoleId = "pc_engine"
            GamesView(consoleId, main.activity.context)
        }
        mainComposable.onNodeWithContentDescription("search_game_text_field").performTextInput(text = "ranma")
        Thread.sleep(2000)
        mainComposable.onNodeWithContentDescription("webview_layout").assertIsDisplayed()
        Thread.sleep(2000)
        mainComposable.onNodeWithContentDescription("next_found").performClick()
        Thread.sleep(2000)
        mainComposable.onNodeWithContentDescription("before_found").performClick()
        Thread.sleep(2000)
        mainComposable.onNodeWithContentDescription("cancel_search_button").performClick()
        Thread.sleep(2000)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun test_on_back_is_working () {
        mainComposable.setContent {
            AndroidView(factory = { context ->
                WebView(context, null)
            })
            val consoleId = "pc_engine"
            GamesView(consoleId, main.activity.context).apply { main.setContent {
                onBackPress(
                    routeViewmodel = routeViewmodel,
                    context = main.activity.context,
                    navController = navController
                ).invoke() }
            }
            Thread.sleep(2000)
        }
    }

    @Test
    fun test_get_console_history_file() {
        var locale = "es_ES"
        val consoleName = "atari_2600"
        var fileName = getConsoleHistoryFile(consoleName, locale)
        assert(fileName.equals("atari_2600_es.pdf"))
        locale = Locale.UK.displayName
        fileName = getConsoleHistoryFile(consoleName, locale)
        assert(fileName.equals("atari_2600_en.pdf"))
    }

    @Test
    fun test_get_console_game_url() {
        val consoleName = "atari_2600"
        val fileName = getConsoleGameUrl(consoleName)
        assert(fileName.equals("file:///android_asset/atari_2600.html"))
    }

    @Test
    fun test_get_console_accesory_file() {
        var locale = "es_ES"
        val consoleName = "atari_2600"
        var fileName = getConsoleAccesoriesFile(consoleName, locale)
        assert(fileName.equals("atari_2600_accessories_es.pdf"))
        locale = Locale.UK.displayName
        fileName = getConsoleAccesoriesFile(consoleName, locale)
        assert(fileName.equals("atari_2600_accessories_en.pdf"))
    }

    @Test
    fun test_get_console_cable_file() {
        var locale = "es_ES"
        val consoleName = "atari_2600"
        var fileName = getConsoleCablesFile(consoleName, locale)
        assert(fileName.equals("atari_2600_cables_es.pdf"))
        locale = Locale.UK.displayName
        fileName = getConsoleCablesFile(consoleName, locale)
        assert(fileName.equals("atari_2600_cables_en.pdf"))
    }

    @Test
    fun test_get_console_mod_file() {
        var locale = "es_ES"
        val consoleName = "atari_2600"
        var fileName = getConsoleModsFile(consoleName, locale)
        assert(fileName.equals("atari_2600_mods_es.pdf"))
        locale = Locale.UK.displayName
        fileName = getConsoleModsFile(consoleName, locale)
        assert(fileName.equals("atari_2600_mods_en.pdf"))
    }

    @Test
    fun test_instantiate_pdf_wrapper() {
        val pdfView = instantiatePDFWrapper(main.activity.context)
        val file = context.assets.open(getConsoleAccesoriesFile("atari_2600","es_ES")).readBytes()
        pdfView.fromBytes(file)
        assert(pdfView.id >0)
    }

    @Test
    fun test_instantiate_webview_wrapper() {
        mainComposable.runOnUiThread {
            val webView = instantiateWebViewWrapper(main.activity.context)
            val file = "file:///android_asset/atari_2600.html"
            webView.loadUrl(file)
            assert(webView.url.equals("file:///android_asset/atari_2600.html"))
        }
    }
}