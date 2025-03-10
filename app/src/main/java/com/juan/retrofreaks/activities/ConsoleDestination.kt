package com.juan.retrofreaks.activities

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.barteksc.pdfviewer.PDFView
import com.juan.retrofreaks.MainActivity
import com.juan.retrofreaks.R
import com.juan.retrofreaks.repository.LanguageViewModel
import com.juan.retrofreaks.repository.RouteViewModel
import kotlinx.coroutines.launch

/*
Contains logic for the Screens relative to the Consoles, including a path to see the Games Screen, Mods, Cables, etcetera.
 */
@ExperimentalComposeUiApi
@Composable
fun ConsoleDestination(myContext:Context, navController: NavController, consoleId: String) {

        val routeViewmodel = RouteViewModel(navController.context)
        val formedRoute = routeViewmodel.route.value ?: ""
        routeViewmodel.setRoute(formedRoute)
        navController.enableOnBackPressed(true)
        val context = myContext //navController.context
        val languageViewModel = LanguageViewModel(navController.context)
        val currentLocale = languageViewModel.locale.value
        val pdfView = instantiatePDFWrapper(context)
        /////////////////////onBack()'s Button callback/////////////
        val onBack = onBackPress(routeViewmodel, context, navController)
        BackHandler(true,onBack)
        ///////////////////END OF ONBACK BUTTON IMPLEMENTATION/////////////////////////////////////
        //Executes destination depending on route
            when (formedRoute.split("/")[2].lowercase()) {
                ACTION_NAME_ARG.lowercase() -> BaseConsoleView(
                    navController,
                    currentLocale,
                    formedRoute.split("/")[1].lowercase(),
                    pdfView
                )
                "games", "juegos" -> GamesView(consoleId, navController.context)
                "accesorios", "accessories" -> AccesoriesView(
                    navController.context,
                    currentLocale,
                    consoleId.lowercase(),
                    pdfView
                )
                stringResource(id = R.string.cables_string).lowercase() -> CablesView(
                    navController,
                    currentLocale,
                    consoleId,
                    pdfView
                )
                stringResource(id = R.string.mods_string).lowercase() -> ModsView(
                    navController,
                    currentLocale,
                    consoleId,
                    pdfView
                )
                else -> ErrorView()
            }
}
////////////////CONSOLE HISTORY/////////////////////////////////////
/*
CALLED WHEN CLICKED ON THE CONSOLE'S NAME. SHOWS ITS HISTORY FROM A PDF FILE.
DEPENDING ON LOCALE, SHOWS SPANISH OR ENGLISH TEXT.
 */

@Composable
fun BaseConsoleView(navController: NavController, currentLocale: String?, consoleId: String, pdfView:PDFView) {
    var fileName by remember { mutableStateOf("")}
    fileName = getConsoleHistoryFile(consoleId, currentLocale)
    val modifier = Modifier

    ConstraintLayout(modifier = modifier.background(color = colorResource(id = R.color.clear_ocre))) {
        val (pdfViewConst) = createRefs()
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.clear_ocre))
                .semantics { contentDescription = "base_console_layout" }
                .constrainAs(pdfViewConst) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, factory = {
                    val data = navController.context.assets.open(fileName).readBytes()
                    pdfView.apply {
                        setBackgroundColor(ContextCompat.getColor(context.applicationContext, R.color.clear_ocre))
                        isSwipeEnabled
                        isPageSnap = true
                        useBestQuality(true)
                        fromBytes(data).load()
                    }
                    pdfView
                })
    }
}


////////////////END OF CONSOLE HISTORY/////////////////////////////////////
////////////////SHOWS GAMES OF EACH CONSOLE ON A WEBVIEW/////////////////////////////////////
@Composable
@ExperimentalComposeUiApi
fun GamesView(consoleId: String?, context: Context) {
    var fileName by remember { mutableStateOf("")}
    fileName = getConsoleGameUrl(consoleId)
    var url by remember{mutableStateOf("")}
    url = fileName
    //Logic for the Game search in the WebView using an EditText 'searchTest'
    val searchText = rememberSaveable{ mutableStateOf("")}
    val isVisibleArrows = remember { mutableStateOf(if (searchText.value != "") true else false) }
    val isSearchOn = rememberSaveable{mutableStateOf(isVisibleArrows.value)}
    val kc = LocalSoftwareKeyboardController.current

    //Setting up the WebView
    val webView = instantiateWebViewWrapper(context)

        webView.apply {
        settings.domStorageEnabled = true
        webViewClient = WebViewClient()
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        isHorizontalScrollBarEnabled = true
        loadUrl(url)
        isScrollContainer = true
    }

    /*
    Attach the WebView to the Layout and ensure search ability works even on configuration
    changes by using lifecycle changes.
     */
    ConstraintLayout(modifier = Modifier
        .semantics { contentDescription = "webview_layout" }
        .background(color = colorResource(id = R.color.clear_ocre))) {
        val (rowref, spaceref, webviewref, buttonsRow, spaceref2) = createRefs()

        AndroidView(modifier = Modifier
            .padding(top = 8.dp)
            .constrainAs(webviewref) { top.linkTo(buttonsRow.bottom) }, factory = { webView })

        val lifecycleOwner : LifecycleOwner = LocalLifecycleOwner.current
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {if (isSearchOn.value){
                    webView.findAllAsync(searchText.value)
                    isVisibleArrows.value = true
                }}
                Lifecycle.Event.ON_DESTROY -> {
                    if (searchText.value.isBlank() || searchText.value == context.resources.getString(R.string.enter_search_string))
                    {
                        isVisibleArrows.value = false
                        searchText.value = ""
                        webView.findAllAsync(context.resources.getString(R.string.null_string))
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        Row(
                modifier = Modifier
                    .constrainAs(rowref) { top.linkTo(parent.top) }
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(8.dp)
            ) {
                TextField(
                    value = searchText.value,
                    maxLines = 1,
                    enabled = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {kc?.hide()}),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                searchText.value = ""
                                isVisibleArrows.value = false

                            },
                            modifier = Modifier.semantics { contentDescription = "cancel_search_button" }.align(CenterVertically)
                        ) {

                            Icon(Icons.Rounded.Close, contentDescription = "", tint = Color.Black)
                        }
                    },
                    onValueChange = {
                        searchText.value = it
                        if (searchText.value.isBlank() || searchText.value == context.resources.getString(R.string.enter_search_string))
                        {
                            isVisibleArrows.value = false
                            searchText.value = ""
                            webView.findAllAsync(context.resources.getString(R.string.null_string))
                        }

                        if (searchText.value != "")
                        {
                                searchText.value = it
                                isSearchOn.value = true
                                webView.findAllAsync(searchText.value)
                                isVisibleArrows.value = true
                        }
                        else
                            isVisibleArrows.value = false
                    },
                    singleLine = true,
                    placeholder = { Text(stringResource(id = R.string.enter_search_string)) },
                    modifier = Modifier.semantics { contentDescription = "search_game_text_field" }
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            colorResource(id = R.color.ocre)
                        ),
                )
            }
            Spacer(
                modifier = Modifier
                    .constrainAs(spaceref) { top.linkTo(rowref.bottom) }
                    .width(500.dp)
                    .border(4.dp, Color.Black)
            )

        //Adds arrows to "NEXT" or "PREVIOUS" item searched for
        Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = CenterVertically, modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth(1f)
            .background(Color.Transparent)
            .alpha(if (isVisibleArrows.value) 1f else 0f)
            .constrainAs(buttonsRow) {
                top.linkTo(spaceref.bottom)
            }
            .padding(top = 8.dp)) {
            IconButton(modifier = Modifier
                .semantics { contentDescription = "next_found" }
                .background(Color.Black)
                .alpha(if (isVisibleArrows.value) 1f else 0f)
                ,onClick = { webView.findNext(false) },
                enabled = isVisibleArrows.value
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "next_found_icon",
                    tint = Color.White,
                    modifier = Modifier.requiredSize(15.dp, 15.dp)
                )
            }
            Text(text = stringResource(id = R.string.previous_next_string), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.alpha(if (isVisibleArrows.value) 1f else 0f))
            IconButton(modifier = Modifier
                .semantics { contentDescription = "before_found" }
                .background(Color.Black)
                .alpha(if (isVisibleArrows.value) 1f else 0f)
                ,onClick = { webView.findNext(true) },
                enabled = isVisibleArrows.value
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    tint = Color.White,
                    contentDescription = "before_found_icon",
                    modifier = Modifier.requiredSize(15.dp, 15.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .constrainAs(spaceref2) { top.linkTo(buttonsRow.bottom) }
                .width(500.dp)
                .border(4.dp, Color.Black)
        )
    }
}
/////////////END OF GAME LIST///////////////////////////////////
///////////LISTING ACCESSORIES BY CONSOLE///////////////////////////
@Composable
fun AccesoriesView(context: Context, currentLocale: String?, consoleId: String, pdfView: PDFView) {
    //Creates layout for presenting PDF files and attaches them.
    ConstraintLayout(modifier = Modifier.background(color = colorResource(id = R.color.clear_ocre))) {
        val (pdfViewConst) = createRefs()
        AndroidView(
            modifier = Modifier
                .semantics { contentDescription = "accesories_view_layout" }
                .fillMaxSize()
                .background(color = colorResource(id = R.color.clear_ocre))
                .constrainAs(pdfViewConst) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, factory = {
                val file = context.assets.open(getConsoleAccesoriesFile(consoleId,currentLocale)).readBytes()
                pdfView.apply {
                    setBackgroundColor(ContextCompat.getColor(context.applicationContext, R.color.clear_ocre))
                    isSwipeEnabled
                    isPageSnap = true
                    useBestQuality(true)
                    fromBytes(file).load()
                }
                pdfView
            })
    }
}
///////////END OF LISTING ACCESSORIES BY CONSOLE///////////////////////////
/////////CABLE LISTING BY CONSOLE////////////
@Composable
fun CablesView(navController: NavController, currentLocale: String?, consoleId: String, pdfView: PDFView) {
    //Creating layout for PDFViews and reaching pdf files required
    ConstraintLayout(modifier = Modifier
        .semantics { contentDescription = "cables_view_layout" }
        .background(color = colorResource(id = R.color.clear_ocre))) {
        val (pdfViewConst) = createRefs()
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.clear_ocre))
                .constrainAs(pdfViewConst) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, factory = {
                val file = navController.context.assets.open(getConsoleCablesFile(consoleId,currentLocale)).readBytes()
                pdfView.apply {
                    setBackgroundColor(ContextCompat.getColor(context.applicationContext, R.color.clear_ocre))
                    isSwipeEnabled
                    isPageSnap = true
                    useBestQuality(true)
                    fromBytes(file).load()
                }
                pdfView
            })
    }
}
//////////////END OF CABLE VIEW////////////////////
//////MODS BY CONSOLE/////////////////
@Composable
fun ModsView(navController: NavController, currentLocale: String?, consoleId: String, pdfView: PDFView) {
    //Layout for PDF File representation
    ConstraintLayout(modifier = Modifier
        .semantics { contentDescription = "mods_view_layout" }
        .background(color = colorResource(id = R.color.clear_ocre))) {
        val (pdfViewConst) = createRefs()
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.clear_ocre))
                .constrainAs(pdfViewConst) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, factory = {
                val file = navController.context.assets.open(getConsoleModsFile(consoleId,currentLocale)).readBytes()
                pdfView.apply {
                    setBackgroundColor(ContextCompat.getColor(context.applicationContext, R.color.clear_ocre))
                    isSwipeEnabled
                    isPageSnap = true
                    useBestQuality(true)
                    fromBytes(file).load()
                }
                pdfView
            })
    }
}
/////////////////ONBACKBUTTON PRESSED CALLBACK///////
@Composable
fun onBackPress(routeViewmodel: RouteViewModel, context: Context, navController: NavController): () -> Unit {
    var closedDrawer:Boolean
    var drawerState:DrawerState
    val coroutineScope = rememberCoroutineScope()
    val onBack = {
        routeViewmodel.setRoute(BASE_SCREEN)
        drawerState = (context as MainActivity).getMyDrawerState()
        closedDrawer = drawerState.isOpen
        if (closedDrawer) {
            if (drawerState.isOpen)
                coroutineScope.launch {
                    drawerState.close()
                    closedDrawer = drawerState.isOpen
                }
        }
        else {
            navController.navigate(BASE_SCREEN) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                popUpTo(0)
            }
        }
    }
    return onBack
}

////////VIEW IF THERES AN ERROR UPON LOADING ANY OF THE PREVIOUS VIEWS (WONT HAPPEN)/////////
@Composable
fun ErrorView() {
    val modifier = Modifier
    val sMFont = Font(R.font.super_mario_bros_font,FontWeight.W400)
    //Creating layout and a text saying "OOOOPS"
    ConstraintLayout(modifier = modifier
        .semantics { contentDescription = "error_view_layout" }
        .fillMaxSize()
        .background(color = colorResource(id = R.color.clear_ocre))) {
        val (text) = createRefs()
        Text(modifier = Modifier.constrainAs(text){
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            absoluteLeft.linkTo(parent.start)
            absoluteRight.linkTo(parent.end)
        },fontFamily = FontFamily(sMFont), textAlign = TextAlign.Center, text = stringResource(id = R.string.opps), fontSize = 50.sp, fontStyle = FontStyle.Italic, color = Color.Black)
    }
}
/////////////////////END OF COMPOSABLES/////////////////////////
//Returns console's History file repending on locale
fun getConsoleHistoryFile(consoleName:String, currentLocale:String?) :String {
    val fileName = when (consoleName) {
        "atari_2600" -> if (currentLocale.equals("es_ES"))
            "atari_2600_es.pdf"
        else
            "atari_2600_en.pdf"
        "dreamcast" -> if (currentLocale.equals("es_ES"))
            "dreamcast_es.pdf"
        else
            "dreamcast_en.pdf"
        "fds" -> if (currentLocale.equals("es_ES"))
            "fds_es.pdf"
        else
            "fds_en.pdf"
        "classic_gb" -> if (currentLocale.equals("es_ES"))
            "classic_gb_es.pdf"
        else
            "classic_gb_en.pdf"
        "gba" -> if (currentLocale.equals("es_ES"))
            "gba_es.pdf"
        else
            "gba_en.pdf"
        "gbc" -> if (currentLocale.equals("es_ES"))
            "gbc_es.pdf"
        else
            "gbc_en.pdf"
        "gamecube" -> if (currentLocale.equals("es_ES"))
            "gamecube_es.pdf"
        else
            "gamecube_en.pdf"
        "gamegear" -> if (currentLocale.equals("es_ES"))
            "gamegear_es.pdf"
        else
            "gamegear_en.pdf"
        "md_genesis" -> if (currentLocale.equals("es_ES"))
            "md_genesis_es.pdf"
        else
            "md_genesis_en.pdf"
        "neogeo" -> if (currentLocale.equals("es_ES"))
            "neogeo_es.pdf"
        else
            "neogeo_en.pdf"
        "nes_famicom" -> if (currentLocale.equals("es_ES"))
            "nes_famicom_es.pdf"
        else
            "nes_famicom_en.pdf"
        "n64" -> if (currentLocale.equals("es_ES"))
            "n64_es.pdf"
        else
            "n64_en.pdf"
        "pc_engine" -> if (currentLocale.equals("es_ES"))
            "pc_engine_es.pdf"
        else
            "pc_engine_en.pdf"
        "playstation2" -> if (currentLocale.equals("es_ES"))
            "playstation2_es.pdf"
        else
            "playstation2_en.pdf"
        "psx" -> if (currentLocale.equals("es_ES"))
            "psx_es.pdf"
        else
            "psx_en.pdf"
        "satellaview" -> if (currentLocale.equals("es_ES"))
            "satellaview_es.pdf"
        else
            "satellaview_en.pdf"
        "saturn" -> if (currentLocale.equals("es_ES"))
            "saturn_es.pdf"
        else
            "saturn_en.pdf"
        "snes" -> if (currentLocale.equals("es_ES"))
            "snes_es.pdf"
        else
            "snes_en.pdf"
        "master_system" -> if (currentLocale.equals("es_ES"))
            "master_system_es.pdf"
        else
            "master_system_en.pdf"
        else -> "empty.pdf"
    }
    return fileName
}
//Returns an html file's name given a console name
fun getConsoleGameUrl(consoleId:String?) : String {
    val fileName = when (consoleId) {
        "atari_2600" -> "file:///android_asset/atari_2600.html"
        "dreamcast" -> "file:///android_asset/dreamcast.html"
        "fds" -> "file:///android_asset/fds.html"
        "classic_gb" -> "file:///android_asset/classic_gb.html"
        "gba" -> "file:///android_asset/gba.html"
        "gbc" -> "file:///android_asset/gbc.html"
        "gamecube" -> "file:///android_asset/gamecube.html"
        "gamegear" -> "file:///android_asset/gamegear.html"
        "md_genesis" -> "file:///android_asset/md_genesis.html"
        "neogeo" -> "file:///android_asset/neogeo.html"
        "nes_famicom" -> "file:///android_asset/nes_famicom.html"
        "n64" -> "file:///android_asset/nintendo64.html"
        "pc_engine" -> "file:///android_asset/pc_engine.html"
        "playstation2" -> "file:///android_asset/playstation2.html"
        "psx" -> "file:///android_asset/psx.html"
        "satellaview" -> "file:///android_asset/satellaview.html"
        "saturn" -> "file:///android_asset/saturn.html"
        "snes" -> "file:///android_asset/snes.html"
        else -> "file:///android_asset/empty.html"
    }
    return fileName
}
//Returns names of PDF files depending on the locale configuration.
fun getConsoleAccesoriesFile(consoleId: String, currentLocale: String?): String {
    return when (consoleId) {
        "atari_2600" -> if (currentLocale.equals("es_ES"))
            "atari_2600_accessories_es.pdf"
        else
            "atari_2600_accessories_en.pdf"
        "dreamcast" -> if (currentLocale.equals("es_ES"))
            "dreamcast_accessories_es.pdf"
        else
            "dreamcast_accessories_en.pdf"

        "fds" -> if (currentLocale.equals("es_ES"))
            "fds_accessories_es.pdf"
        else
            "fds_accessories_en.pdf"
        "classic_gb" -> if (currentLocale.equals("es_ES"))
            "classic_gb_accessories_es.pdf"
        else
            "classic_gb_accessories_en.pdf"
        "gba" -> if (currentLocale.equals("es_ES"))
            "gba_accessories_es.pdf"
        else
            "gba_accessories_en.pdf"
        "gbc" -> if (currentLocale.equals("es_ES"))
            "gbc_accessories_es.pdf"
        else
            "gbc_accessories_en.pdf"
        "gamecube" -> if (currentLocale.equals("es_ES"))
            "gamecube_accessories_es.pdf"
        else
            "gamecube_accessories_en.pdf"
        "gamegear" -> if (currentLocale.equals("es_ES"))
            "gamegear_accessories_es.pdf"
        else
            "gamegear_accessories_en.pdf"

        "md_genesis" -> if (currentLocale.equals("es_ES"))
            "md_genesis_accessories_es.pdf"
        else
            "md_genesis_accessories_en.pdf"
        "neogeo" -> if (currentLocale.equals("es_ES"))
            "neogeo_accessories_es.pdf"
        else
            "neogeo_accessories_en.pdf"
        "nes_famicom" -> if (currentLocale.equals("es_ES"))
            "nes_famicom_accessories_es.pdf"
        else
            "nes_famicom_accessories_en.pdf"
        "n64" -> if (currentLocale.equals("es_ES"))
            "n64_accessories_es.pdf"
        else
            "n64_accessories_en.pdf"
        "pc_engine" -> if (currentLocale.equals("es_ES"))
            "pc_engine_accessories_es.pdf"
        else
            "pc_engine_accessories_en.pdf"
        "playstation2" -> if (currentLocale.equals("es_ES"))
            "playstation2_accessories_es.pdf"
        else
            "playstation2_accessories_en.pdf"
        "psx" -> if (currentLocale.equals("es_ES"))
            "psx_accessories_es.pdf"
        else
            "psx_accessories_en.pdf"
        "satellaview" -> if (currentLocale.equals("es_ES"))
            "satellaview_accessories_es.pdf"
        else
            "satellaview_accessories_en.pdf"
        "saturn" -> if (currentLocale.equals("es_ES"))
            "saturn_accessories_es.pdf"
        else
            "saturn_accessories_en.pdf"
        "snes" -> if (currentLocale.equals("es_ES"))
            "snes_accessories_es.pdf"
        else
            "snes_accessories_en.pdf"
        "master_system" -> if (currentLocale.equals("es_ES"))
            "master_system_accessories_es.pdf"
        else
            "master_system_accessories_en.pdf"
        else -> "empty.pdf"
    }
}

//files for CablesView by locale
fun getConsoleCablesFile(consoleId: String, currentLocale: String?): String {
    return when (consoleId) {
        "atari_2600" -> if (currentLocale.equals("es_ES"))
            "atari_2600_cables_es.pdf"
        else
            "atari_2600_cables_en.pdf"
        "dreamcast" -> if (currentLocale.equals("es_ES"))
            "dreamcast_cables_es.pdf"
        else
            "dreamcast_cables_en.pdf"
        "fds" -> if (currentLocale.equals("es_ES"))
            "fds_cables_es.pdf"
        else
            "fds_cables_en.pdf"
        "classic_gb" -> if (currentLocale.equals("es_ES"))
            "classic_gb_cables_es.pdf"
        else
            "classic_gb_cables_en.pdf"
        "gba" -> if (currentLocale.equals("es_ES"))
            "gba_cables_es.pdf"
        else
            "gba_cables_en.pdf"
        "gbc" -> if (currentLocale.equals("es_ES"))
            "gbc_cables_es.pdf"
        else
            "gbc_cables_en.pdf"
        "gamecube" -> if (currentLocale.equals("es_ES"))
            "gamecube_cables_es.pdf"
        else
            "gamecube_cables_en.pdf"
        "gamegear" -> if (currentLocale.equals("es_ES"))
            "gamegear_cables_es.pdf"
        else
            "gamegear_cables_en.pdf"
        "md_genesis" -> if (currentLocale.equals("es_ES"))
            "md_genesis_cables_es.pdf"
        else
            "md_genesis_cables_en.pdf"
        "neogeo" -> if (currentLocale.equals("es_ES"))
            "neogeo_cables_es.pdf"
        else
            "neogeo_cables_en.pdf"
        "nes_famicom" -> if (currentLocale.equals("es_ES"))
            "nes_famicom_cables_es.pdf"
        else
            "nes_famicom_cables_en.pdf"
        "n64" -> if (currentLocale.equals("es_ES"))
            "n64_cables_es.pdf"
        else
            "n64_cables_en.pdf"
        "pc_engine" -> if (currentLocale.equals("es_ES"))
            "pc_engine_cables_es.pdf"
        else
            "pc_engine_cables_en.pdf"
        "playstation2" -> if (currentLocale.equals("es_ES"))
            "playstation2_cables_es.pdf"
        else
            "playstation2_cables_en.pdf"
        "psx" -> if (currentLocale.equals("es_ES"))
            "psx_cables_es.pdf"
        else
            "psx_cables_en.pdf"
        "satellaview" -> if (currentLocale.equals("es_ES"))
            "satellaview_cables_es.pdf"
        else
            "satellaview_cables_en.pdf"
        "saturn" -> if (currentLocale.equals("es_ES"))
            "saturn_cables_es.pdf"
        else
            "saturn_cables_en.pdf"
        "snes" -> if (currentLocale.equals("es_ES"))
            "snes_cables_es.pdf"
        else
            "snes_cables_en.pdf"
        "master_system" -> if (currentLocale.equals("es_ES"))
            "master_system_cables_es.pdf"
        else
            "master_system_cables_en.pdf"
        else -> "empty.pdf"
    }
}

//PDF files for ModView organized by locale
fun getConsoleModsFile(consoleId: String, currentLocale: String?): String {
    return when (consoleId) {
        "atari_2600" -> if (currentLocale.equals("es_ES"))
            "atari_2600_mods_es.pdf"
        else
            "atari_2600_mods_en.pdf"
        "dreamcast" -> if (currentLocale.equals("es_ES"))
            "dreamcast_mods_es.pdf"
        else
            "dreamcast_mods_en.pdf"
        "fds" -> if (currentLocale.equals("es_ES"))
            "fds_mods_es.pdf"
        else
            "fds_mods_en.pdf"
        "classic_gb" -> if (currentLocale.equals("es_ES"))
            "classic_gb_mods_es.pdf"
        else
            "classic_gb_mods_en.pdf"
        "gba" -> if (currentLocale.equals("es_ES"))
            "gba_mods_es.pdf"
        else
            "gba_mods_en.pdf"
        "gbc" -> if (currentLocale.equals("es_ES"))
            "gbc_mods_es.pdf"
        else
            "gbc_mods_en.pdf"
        "gamecube" -> if (currentLocale.equals("es_ES"))
            "gamecube_mods_es.pdf"
        else
            "gamecube_mods_en.pdf"
        "gamegear" -> if (currentLocale.equals("es_ES"))
            "gamegear_mods_es.pdf"
        else
            "gamegear_mods_en.pdf"
        "md_genesis" -> if (currentLocale.equals("es_ES"))
            "md_genesis_mods_es.pdf"
        else
            "md_genesis_mods_en.pdf"
        "neogeo" -> if (currentLocale.equals("es_ES"))
            "neogeo_mods_es.pdf"
        else
            "neogeo_mods_en.pdf"
        "nes_famicom" -> if (currentLocale.equals("es_ES"))
            "nes_famicom_mods_es.pdf"
        else
            "nes_famicom_mods_en.pdf"
        "n64" -> if (currentLocale.equals("es_ES"))
            "n64_mods_es.pdf"
        else
            "n64_mods_en.pdf"
        "pc_engine" -> if (currentLocale.equals("es_ES"))
            "pc_engine_mods_es.pdf"
        else
            "pc_engine_mods_en.pdf"
        "playstation2" -> if (currentLocale.equals("es_ES"))
            "playstation2_mods_es.pdf"
        else
            "playstation2_mods_en.pdf"
        "psx" -> if (currentLocale.equals("es_ES"))
            "psx_mods_es.pdf"
        else
            "psx_mods_en.pdf"
        "satellaview" -> if (currentLocale.equals("es_ES"))
            "satellaview_mods_es.pdf"
        else
            "satellaview_mods_en.pdf"
        "saturn" -> if (currentLocale.equals("es_ES"))
            "saturn_mods_es.pdf"
        else
            "saturn_mods_en.pdf"
        "snes" -> if (currentLocale.equals("es_ES"))
            "snes_mods_es.pdf"
        else
            "snes_mods_en.pdf"
        "master_system" -> if (currentLocale.equals("es_ES"))
            "master_system_mods_es.pdf"
        else
            "master_system_mods_en.pdf"
        else -> "empty.pdf"
    }
}

///////////END OF MODS VIEW////////////////////
//Instantiates and configure Pdf Files
fun instantiatePDFWrapper(context: Context): PDFView {
    val pdfViewWrapper = (context as MainActivity).getMyPDFViewWrapper()
    return pdfViewWrapper.getWPDFView()
}

//Instantiates and configure Pdf Files
fun instantiateWebViewWrapper(context:Context) : WebView {
    val webViewWrapper = (context as MainActivity).getMyWebViewWrapper()
    return webViewWrapper.getWebView()
}

