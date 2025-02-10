package com.juan.retrofreaks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.barteksc.pdfviewer.PDFView
import com.juan.retrofreaks.navigation.SetupNavGraph
import com.juan.retrofreaks.repository.LanguageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.juan.retrofreaks.activities.*
import com.juan.retrofreaks.categories.PDFViewWrapper
import com.juan.retrofreaks.categories.WebViewWrapper
import com.juan.retrofreaks.repository.RouteViewModel
import androidx.lifecycle.LifecycleEventObserver
import com.juan.retrofreaks.repository.ExecuteInitialAnimationViewModel

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {

    lateinit var navHostController:NavHostController
    lateinit var context: Context
    lateinit var timesBackPressed: MutableState<Int>
    lateinit var scaffoldState:ScaffoldState
    lateinit var languageViewModel:LanguageViewModel
    lateinit var routeViewModel: RouteViewModel
    lateinit var locale: String
    var webView: WebView? = null
    lateinit var webViewRoot: View
    var pdfView:PDFView? = null
    lateinit var pdfViewRoot: View
    lateinit var openDialog:MutableState<Boolean>
    lateinit var isSwitchEnabled:MutableState<Boolean>
    lateinit var drawerState:DrawerState
    private lateinit var animationViewModel:ExecuteInitialAnimationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        Instantiate PDFView and WebView. NOT DOING SO WILL MAKE THE APP CRASH UPON
        CHANGE OF CONFIGURATION as the PDFFilew and WebView's Context is stale.
         */
        instantiatePdfViewRoot()
        instantiateWebViewRoot()

        setContent {
            onBackPressedDispatcher
            context = LocalContext.current
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            openDialog = remember { mutableStateOf(false)  }
            isSwitchEnabled = remember { mutableStateOf(false)}
            languageViewModel = LanguageViewModel(context)
            routeViewModel = RouteViewModel(context)
            routeViewModel.setRoute(BASE_SCREEN)
            locale = languageViewModel.locale.value!!
            languageViewModel.setLocale(locale,context)
            val coroutineScope = rememberCoroutineScope()
            timesBackPressed = remember{ mutableStateOf(0) }
            animationViewModel = ExecuteInitialAnimationViewModel(context)
            lifecycle.addObserver(getLifecycleEventObserver())
            /////////////////////////////////////////////////////////////////////////////////////
            //Enables pressing of back button
            val onBack = onBackPress(coroutineScope = coroutineScope, context = context)
            BackHandler(true,onBack)
            //starts the Drawer
            StartDrawer()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun StartDrawer() {
        navHostController = rememberNavController()
        navHostController.enableOnBackPressed(enabled = true)
        val menuItems = getConsoleNameList()

        scaffoldState = rememberScaffoldState()

        val scope = rememberCoroutineScope()

        Scaffold(modifier = Modifier.semantics { testTag = "scaffold_main" }, scaffoldState = scaffoldState,
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            contentColor = Color.Black,
            content = {SetupNavGraph(this@MainActivity.context, navController = navHostController) },
            topBar = { TopBar(scaffoldState,scope)},
            drawerContent = {DrawerDetails(menuItems = menuItems)})
        }


    @Composable
    fun TopBar(scaffoldState: ScaffoldState, scope: CoroutineScope) {
        drawerState = scaffoldState.drawerState
        val modifier = Modifier
        Row(modifier = modifier) {
            TopAppBar(
                navigationIcon = {
                    IconButton(content = {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "open_drawer_button",
                            modifier = modifier.background(colorResource(id = R.color.ocre))
                        )
                    }, onClick = {
                        scope.launch {
                                if (scaffoldState.drawerState.isClosed)
                                    scaffoldState.drawerState.open()
                                else scaffoldState.drawerState.close()
                            }
                        }
                    )
                },
                title = { Text(stringResource(id = R.string.app_name), color = colorResource(id = R.color.ocre)) },
                actions = {
                    OverFlowMenu()
                } ,
                backgroundColor = colorResource(id = androidx.appcompat.R.color.primary_material_dark)
            , modifier = Modifier
                    .semantics { testTag = "drawer" }
                    .semantics { contentDescription = "open and close" })
        }
    }
////////////////////////////////////////////////////////////////////
    @Composable
    fun OverFlowMenu() {
        isSwitchEnabled.value = languageViewModel.locale.value!!.equals("en_GB")
        val modifier = Modifier

        Row {
            Image(painter = painterResource(id = R.drawable.spains_flag_48p), contentDescription = "spanish",modifier = modifier
                .padding(3.dp, 3.dp, 3.dp, 15.dp)
                .width(50.dp)
                .height(50.dp)
                .align(CenterVertically))
            Switch( modifier = Modifier
                .background(color = colorResource(id = R.color.ocre)),checked = isSwitchEnabled.value, onCheckedChange =
            {
                openDialog.value = true
                    restartDialog()
                if (it == true)
                    languageViewModel.setLocale("en_GB",context)
                else
                    languageViewModel.setLocale("es_ES", context)

            }, colors = SwitchDefaults.colors( checkedThumbColor = colorResource(id = androidx.appcompat.R.color.primary_material_dark), uncheckedThumbColor = colorResource(
                id = androidx.appcompat.R.color.primary_material_dark))
            )
            Image(painter = painterResource(id = R.drawable.united_kingdoms_flag_48p), contentDescription = "english",modifier = Modifier
                .padding(3.dp, 3.dp, 3.dp, 15.dp)
                .width(50.dp)
                .height(50.dp)
                .align(CenterVertically))
        }
    }

    ///////CREATES DRAWER, A LIST OF CONSOLES AND DROPDOWN MENUS FOR EACH OF THEM//
    @Composable
    fun DrawerDetails(menuItems: List<String>) {

        val ocre = Color(0xd1bf7500)
        val helperScoper = rememberCoroutineScope()
        val modifier = Modifier
        var clickedConsole = -1//MEANS NOTHING HAS BEEN CLICKED ON YET
        val isClickedMisc = remember { mutableStateOf(false) }
        val listItemsDropDown = listOf(stringResource(id = R.string.games_string), stringResource(id = R.string.accesories_string),stringResource(id = R.string.cables_string),stringResource(id = R.string.mods_string))
        val listMiscItemsDropDown = listOf(stringResource(id = R.string.flashcarts), stringResource(id = R.string.upscalers_string),stringResource(id = R.string.where_to_buy),stringResource(id = R.string.links_of_interest),
            stringResource(id = R.string.about_string))
        val isDropDownExpandedList = remember { mutableStateListOf(
            false,false,false,false,false,false,false,false,false,
            false,false,false,false,false,false,false,false,false,false
        ) }

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .background(
                    shape = MaterialTheme.shapes.small,
                    color = colorResource(id = R.color.ocre)
                ),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(modifier = Modifier
                .padding(8.dp, 8.dp)
                , painter = painterResource(id = R.drawable.retrofreaks_icon), contentDescription = "console_icon")
            //////////////////////////////CONSOLE NAME/////////////////////////////////
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .width(500.dp)
                .height(4.dp)
                .border(4.dp, Color.Black))
            Column(modifier = modifier.align(CenterHorizontally)){

            //List all consoles
            for (i in 0..menuItems.indices.last){
                val onClick= {
                    instantiateWebViewRoot()
                    instantiatePdfViewRoot()
                    val route = (Screen.Console.getBaseScreen()+"/"+menuItems[i].replace(" ","_")+"/"+ACTION_NAME_ARG).lowercase()
                    helperScoper.launch {
                        scaffoldState.drawerState.close()
                    }
                    if (!routeViewModel.equals(route)){
                        navHostController.navigate(Screen.Home.getBaseScreen()) {popUpTo(Screen.Home.getBaseScreen()) {inclusive = true} }
                        navHostController.navigate(route)
                    }
                    else
                        navHostController.navigate(route){
                             launchSingleTop = true
                        }
                    navHostController.currentDestination?.route?.let { routeViewModel.setRoute(it) }
                }

                    Row(modifier = Modifier.clickable {
                            clickedConsole = i
                            onClick.invoke() },
                                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = CenterVertically){
                        Image(painter = painterResource(getIconResources(consoleName = menuItems[i])), modifier = Modifier
                            .size(64.dp)
                            .padding(start = 10.dp), contentDescription = "console_icon")
                        Text(modifier = modifier.padding(start = 10.dp), textAlign = TextAlign.Center, style = TextStyle(Color.Black, shadow = Shadow(Color.Red)), text = menuItems[i], fontStyle = FontStyle.Italic, textDecoration = TextDecoration.Underline, fontSize = 20.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = isDropDownExpandedList[i],
                                    onExpandedChange = {
                                        isDropDownExpandedList[i] = !isDropDownExpandedList[i]
                                    }) {
                                    TextField(value = "", modifier = Modifier
                                        .width(180.dp)
                                        .align(CenterVertically), onValueChange = {}, readOnly = true, label = {
                                        Text(
                                            text = stringResource(id = R.string.menu_string),
                                            fontSize = 12.sp
                                        )
                                    }, trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            isDropDownExpandedList[0]
                                        )
                                    })
                                    //List options for each console. Games, Accesories, cables and mods
                                    ExposedDropdownMenu(
                                        expanded = isDropDownExpandedList[i], modifier = Modifier.background(ocre),
                                        onDismissRequest = { isDropDownExpandedList[i] = false }) {
                                        for (j in 0..3) {
                                            val text = listItemsDropDown[j]
                                            DropdownMenuItem(onClick = {
                                                isDropDownExpandedList[i] = false }, modifier = Modifier.border(3.dp,Color.Black,
                                                RectangleShape)) {
                                                    Text(modifier = modifier
                                                        .padding(start = 10.dp)
                                                        .clickable {
                                                            instantiatePdfViewRoot()
                                                            instantiateWebViewRoot()
                                                            //////////////////////////////////
                                                            val consoleRoute =
                                                                (CONSOLE_SCREEN + "/" + menuItems[i].replace(
                                                                    " ",
                                                                    "_"
                                                                ) + "/" + text).lowercase()

                                                            helperScoper.launch {
                                                                isDropDownExpandedList[i] = false
                                                                scaffoldState.drawerState.close()
                                                            }
                                                            if (!routeViewModel.equals(consoleRoute)) {
                                                                navHostController.navigate(Screen.Home.getBaseScreen()) {
                                                                    popUpTo(Screen.Home.getBaseScreen()) {
                                                                        inclusive = true
                                                                    }
                                                                }
                                                                navHostController.navigate(
                                                                    consoleRoute
                                                                )
                                                            } else
                                                                navHostController.navigate(
                                                                    consoleRoute
                                                                ) { launchSingleTop = true }
                                                            navHostController.currentDestination?.route?.let {
                                                                routeViewModel.setRoute(
                                                                    it
                                                                )
                                                            }
                                                        },
                                                        textAlign = TextAlign.Center,
                                                        style = TextStyle(Color.Black,
                                                        shadow = Shadow(Color.Red)),
                                                        text = text,
                                                        fontSize = 20.sp,
                                                        color = Color.Black,
                                                        textDecoration = TextDecoration.Underline
                                                    )
                                            }
                                        }
                                    }
                                }
                            }
                    }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .width(500.dp)
                    .height(4.dp)
                    .border(4.dp, Color.Black))
                }
            }
            ///////////END OF CONSOLES SECTION////////////////////
            ///////////////////////MISCELLANEOUS SECTION////////////////////
            Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = CenterVertically) {
                //Miscelaneous Icon and title
                Image(painter = painterResource(id = R.drawable.misc_icon),
                    modifier = Modifier
                        .size(64.dp)
                        .padding(start = 10.dp), contentDescription = "misc_icon")
                Text(modifier = modifier.padding(start = 10.dp)
                    , textAlign = TextAlign.Center, style = TextStyle(Color.Black, shadow = Shadow(Color.Red))
                    , text = stringResource(id = R.string.misc), fontStyle = FontStyle.Italic
                    , textDecoration = TextDecoration.Underline, fontSize = 20.sp)

                //Creates dropdown menu for miscellaneous
                Row(modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp), horizontalArrangement = Arrangement.End) {
                    ExposedDropdownMenuBox(
                        expanded = isClickedMisc.value,
                        onExpandedChange = {
                            isClickedMisc.value = !isClickedMisc.value
                        }) {
                        TextField(value = "",
                            modifier = Modifier
                                .width(180.dp)
                                .align(CenterVertically),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(text = stringResource(id = R.string.menu_string), fontSize = 12.sp)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    isDropDownExpandedList[0]
                                )
                            })
                        ExposedDropdownMenu(
                            expanded = isClickedMisc.value, modifier = Modifier
                                .background(
                                    ocre
                                )
                                .border(
                                    border = BorderStroke(2.dp, Color.Black),
                                    RoundedCornerShape(bottomEnd = 15.dp)
                                ),
                            onDismissRequest = { isClickedMisc.value = false }) {

                            //Exposes options for dropdownmenu
                            for (k in 0..listMiscItemsDropDown.indices.last) {
                                instantiatePdfViewRoot()
                                val text = listMiscItemsDropDown[k]
                                val miscRoute = listMiscItemsDropDown[k].replace(" ","_").lowercase()

                                DropdownMenuItem(
                                    onClick = {
                                        isClickedMisc.value = false
                                    }, modifier = Modifier.border(3.dp, Color.Black, RectangleShape)
                                ) {
                                    Text(
                                        modifier = modifier
                                            .align(CenterVertically)
                                            .padding(start = 10.dp)
                                            .fillMaxSize()
                                            .clickable {
                                                //Create routes for each option
                                                val route: String
                                                if (miscRoute.equals("flashcarts/ode"))
                                                    route =
                                                        MISC_SCREEN + "/" + "flashcarts" + "/" + MISC_EMPTY
                                                else
                                                    route =
                                                        MISC_SCREEN + "/" + miscRoute + "/" + MISC_EMPTY
                                                helperScoper.launch {
                                                    isClickedMisc.value = false
                                                    scaffoldState.drawerState.close()
                                                }
                                                navHostController.navigate(route) {
                                                    popUpTo(BASE_SCREEN) { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            },
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(Color.Black, shadow = Shadow(Color.Red)),
                                        text = text, fontSize = 15.sp, color = Color.Black, textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                        }
                    }}
                 }
            }
        }
    ////////////////END OF MISCELLANEOUS SECTION///////////////////
    /////////////////ONBACKBUTTON PRESSED CALLBACK///////
    @Composable
    fun onBackPress(coroutineScope:CoroutineScope, context: Context): () -> Unit {
        val onBack = {
            if (scaffoldState.drawerState.isOpen) {
                coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
                if (timesBackPressed.value == 1)
                    timesBackPressed.value--
            }
            else {
                timesBackPressed.value++
                if (timesBackPressed.value<2)
                    Toast.makeText(context, context.getText(R.string.press_to_exit_string),Toast.LENGTH_SHORT).show()
                else
                    (context as Activity).finish()
            }
        }
        return onBack
    }
    /////////////////////END OF COMPOSABLE FUNCTIONS/////////////
    /**
     * Creates a dialog that asks us about changing the app's language
     */
    fun restartDialog(){
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(R.string.change_language)
            .setMessage(R.string.sure_change_language)
            .setNegativeButton(R.string.no, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    openDialog.value = false
                    dialog?.cancel()
                }
            })
            .setPositiveButton(R.string.yes,object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    restartApp(context)
                    isSwitchEnabled.value = !isSwitchEnabled.value
                }
            }).create().show()
    }

    /**
     * Called when language is changed so it resets the changes
     */
    fun restartApp(context:Context) {
        val intent = Intent(context,MainActivity::class.java)
        context.startActivity(intent)
        (context as MainActivity).finishAffinity()
    }
    ///////////////INSTANTIATION OF PDFView AND WebView//////////////////
    fun instantiatePdfViewRoot() {
        pdfViewRoot = layoutInflater.inflate(R.layout.pdf_view_layout,null)
        pdfView = pdfViewRoot.findViewById(R.id.pdfView)
    }

    fun getMyPDFViewWrapper() = PDFViewWrapper(pdfView,pdfViewRoot)

    fun instantiateWebViewRoot() {
        webViewRoot = layoutInflater.inflate(R.layout.webview_layout,null)
        webView = webViewRoot.findViewById(R.id.games_webview)
    }

    fun getMyWebViewWrapper() = WebViewWrapper(webView, webViewRoot)
    ////////////////////////////////////////////////////////////////////

    //Return console names for the List in the Drawer
    fun getConsoleNameList() : List<String> = mutableListOf(
        "Atari 2600","Dreamcast", "FDS", "Classic GB", "GBA",
        "GBC", "Gamecube", "Gamegear", "MD Genesis", "Neogeo",
        "Nes Famicom", "N64", "PC Engine", "Playstation2", "PSX",
        "SatellaView", "Saturn", "Snes","Master System"
    )

    //Icon Resource for each console option in the drawer depending on its name
    fun getIconResources(consoleName:String) : Int {
        val consoleRes = when (consoleName) {
            "Atari 2600" -> R.drawable.atari2600
            "Dreamcast" -> R.drawable.dreamcast_icon
            "FDS" -> R.drawable.famicomdisksystem
            "Classic GB" -> R.drawable.gameboy
            "GBA" -> R.drawable.gba
            "GBC" -> R.drawable.gameboycolor
            "Gamecube" -> R.drawable.gamecube_silver_icon
            "Gamegear" -> R.drawable.gamegear
            "MD Genesis" -> R.drawable.megadrive
            "Neogeo" -> R.drawable.neo_geo_icon
            "Nes Famicom" -> R.drawable.nes_gray_icon
            "N64" -> R.drawable.nintendo_64_black_icon
            "PC Engine" -> R.drawable.pcengine
            "Playstation2" -> R.drawable.ps2_black_icon
            "PSX" -> R.drawable.psx_1_icon
            "SatellaView" -> R.drawable.satellaview
            "Saturn" -> R.drawable.saturn
            "Snes" -> R.drawable.snes
            "Master System" -> R.drawable.master_system_ii
            else -> R.drawable.nothing_icon
        }
        return consoleRes
    }

    /**
    * Returns the state of the drawable. Useful to know its state when it was
     * called by anyone of out the MainActivity (like one of the destinations
     * in the NavHost). We use this function to know we need to close the
     * drawer when clicking ouutside of it or the back button whilst
     * being in one of the other destinations but BaseDestination.
     */
    fun getMyDrawerState() = drawerState

    /**
     * CONTROLS ACTIVITY'S LIFECYCLE TO WRITE IN SHAREDPREFERENCES
     */
    fun getLifecycleEventObserver() : LifecycleEventObserver{
        val lifecycleObserver = LifecycleEventObserver {_, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME ->  Log.i("SHOWINGIT",animationViewModel.isInitialAnimationExecuting.value.toString())
                Lifecycle.Event.ON_STOP ->  animationViewModel.setIsInitialAnimationExecuting(false)
                Lifecycle.Event.ON_DESTROY ->  animationViewModel.setIsInitialAnimationExecuting(true)
                else -> {}
            }
        }
        return lifecycleObserver
    }
}







