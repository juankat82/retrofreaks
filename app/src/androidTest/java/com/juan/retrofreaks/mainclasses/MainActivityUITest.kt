package com.juan.retrofreaks.mainclasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.junit4.*
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.retrofreaks.MainActivity
import com.juan.retrofreaks.navigation.SetupNavGraph
import kotlinx.coroutines.*
import org.junit.*

class MainActivityUITest {

    @get:Rule
    val main = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val composeTestRule:ComposeContentTestRule = createComposeRule()

    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_restart_dialog() {
        Thread.sleep(800)
        main.activity.setContent {
            (main.activity.context as MainActivity).restartDialog()
            Thread.sleep(800)
        }
    }

    @Test
    fun test_restart_app() {
        main.activity.setContent {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as MainActivity).finishAffinity()
        }
    }

    @Test
    fun test_get_console_name_list() {
        val consoleList = (main.activity.context as MainActivity).getConsoleNameList()
        Log.i("TAGME","${consoleList}")
        assert(consoleList.get(0).equals("Atari 2600"))
    }

    @Test
    fun test_get_icon_resources() {
        val myIcon = (main.activity.context as MainActivity).getIconResources("Atari 2600")
        Log.i("MYICON","${myIcon} == ${com.juan.retrofreaks.R.drawable.atari2600}")
        assert(myIcon==com.juan.retrofreaks.R.drawable.atari2600)
    }

    @Test
    fun get_drawer_state() {

        val drawerState = (main.activity.context as MainActivity).getMyDrawerState()
        Log.i("DRAWERIS",drawerState.isOpen.toString())
        assert(drawerState.currentValue == DrawerValue.Closed)
    }

    @Test
    fun test_back_button() {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    main.activity.onBackPressed()
                    main.activity.onBackPressed()
                }
            }
        Thread.sleep(1000)
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Test
    fun test_drawer() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
            main.activity.setContent {
                composeTestRule.setContent {
                        val scope = rememberCoroutineScope()
                        val scaffoldState = ScaffoldState(rememberDrawerState(initialValue = DrawerValue.Open),SnackbarHostState())
                        main.activity.StartDrawer().apply {
                            val navHostController = rememberNavController()
                            navHostController.enableOnBackPressed(enabled = true)
                            Scaffold() {
                                Scaffold(modifier = Modifier.semantics {
                                    testTag = "scaffold_main"
                                },
                                    scaffoldState = scaffoldState,
                                    drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                                    contentColor = Color.Black,
                                    content = {
                                        SetupNavGraph(
                                            main.activity,
                                            navController = navHostController
                                        )
                                    },
                                    topBar = { main.activity.TopBar(scaffoldState, scope) },
                                    drawerContent = { main.activity.DrawerDetails(menuItems = mutableListOf(
                                        "Atari 2600","Dreamcast", "FDS", "Classic GB", "GBA",
                                        "GBC", "Gamecube", "Gamegear", "MD Genesis", "Neogeo",
                                        "Nes Famicom", "N64", "PC Engine", "Playstation2", "PSX",
                                        "SatellaView", "Saturn", "Snes","Master System"
                                    )) })
                            }
                            val scope = rememberCoroutineScope()
                            val del = scope.async { delay(2000) }
                            launch { del.await() }
                        }
                    }
                }
            }
        }
    }
}
