package com.juan.retrofreaks.activities

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.barteksc.pdfviewer.PDFView
import com.juan.retrofreaks.MainActivity
import com.juan.retrofreaks.R
import com.juan.retrofreaks.repository.LanguageViewModel
import com.juan.retrofreaks.repository.RouteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
/*
This class contains logic to create view on the "Miscelaneous" menu down in the DrawerLayout
 */
@Composable
fun MiscDestination(navController: NavController) {
        val routeViewmodel =  RouteViewModel(navController.context)
        val formedRoute = routeViewmodel.route.value ?: ""
        routeViewmodel.setRoute(formedRoute)
        val context = navController.context
        val drawerState:DrawerState = (context as MainActivity).getMyDrawerState()
        val languageViewModel = LanguageViewModel(navController.context)
        val currentLocale = languageViewModel.locale.value
        navController.enableOnBackPressed(true)
        val coroutineScope = rememberCoroutineScope()
        val route = routeViewmodel.route.value!!.split("/").get(1).lowercase()
        //Setting up the PDFView
        val pdfView = instantiatePDFWrapper(context)
        //Executes the function required depending on the given route.
        when(route) {
                "flashcarts" -> FlashcartView(navController = navController, route = route, locale = currentLocale, pdfView)
                "upscalers" -> UpscalersView(navController = navController, route = route, locale = currentLocale, pdfView)
                "where_to_buy","donde_comprar" -> WhereToBuyView(navController = navController, route = route, locale = currentLocale, pdfView)
                "links","enlaces" -> LinksView(navController = navController, route = route, locale = currentLocale, pdfView)
                "about","sobre" -> AboutView(navController = navController, route = route, locale = currentLocale, pdfView)
        }

        /**
        * Enables the execution of the 'onBack' funcion when clicking the Back Button
        * Callback to execute when clicking the 'Back Button'
        */
        val onBack = onBackPressed(routeViewmodel, drawerState,navController,coroutineScope)
        BackHandler(true,onBack)
}

/*
This function shows the contents of the "Flascart/ODE" option
 */
@Composable
fun FlashcartView(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
    MiscLayout(navController = navController, route = route, locale = locale, pdfView = pdfView)
}
/*
This function shows the contents of the "Upscalers" option
 */
@Composable
fun UpscalersView(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
    MiscLayout(navController = navController, route = route, locale = locale, pdfView = pdfView)
}
/*
This function shows the contents of the "Where to buy/Donde comprar" option
 */
@Composable
fun WhereToBuyView(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
    MiscLayout(navController = navController, route = route, locale = locale, pdfView = pdfView)
}
/*
This function shows the contents of the "Links/Enlaces" option
 */
@Composable
fun LinksView(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
    MiscLayout(navController = navController, route = route, locale = locale, pdfView = pdfView)
}
/*
This function shows the contents of the "About/Sobre" (Disclaimer) option
 */
@Composable
fun AboutView(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
       MiscLayout(navController = navController, route = route, locale = locale, pdfView = pdfView)
}
/**
 * Base function containing layout. Will be called with different destination pdf file.
 */
@Composable
fun MiscLayout(navController:NavController, route:String, locale:String?, pdfView: PDFView) {
    //Layout creation
    ConstraintLayout(modifier = Modifier.background(color = colorResource(id = R.color.clear_ocre))) {
        val (pdfViewConst) = createRefs()
        //Attaching PDFView
        AndroidView(modifier = Modifier
            .semantics { contentDescription = "pdf_view" }
            .fillMaxSize()
            .background(color = colorResource(id = R.color.clear_ocre))
            .constrainAs(pdfViewConst) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                absoluteLeft.linkTo(parent.absoluteLeft)
                absoluteRight.linkTo(parent.absoluteRight)
            }, factory = {
            val data = navController.context.assets.open(getFileName(route,locale)).readBytes()
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
/**
 * Handles BackButton
 */
@Composable
fun onBackPressed(routeViewmodel:RouteViewModel, drawerState:DrawerState,navController: NavController, coroutineScope: CoroutineScope) : ()-> Unit {
    val onBack = {
        var closedDrawer:Boolean = drawerState.isOpen
        routeViewmodel.setRoute(BASE_SCREEN)
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
/////////////////ONLY NON COMPOSABLES FROM HERE///////////////////////////
/*
Returns the PDF File required depending on a given Locale
 */
fun getFileName(miscFileName:String, currentLocaleString:String?) :String {
    var fileName = ""
    when(miscFileName) {
        "flashcarts" -> fileName = if (currentLocaleString.equals("es_ES"))
                                        "flashcarts_es.pdf"
                                   else
                                        "flashcarts_en.pdf"
        "upscalers" -> fileName = if (currentLocaleString.equals("es_ES"))
                                        "upscalers_es.pdf"
                                  else
                                        "upscalers_en.pdf"
        "where_to_buy","donde_comprar" -> fileName = if (currentLocaleString.equals("es_ES"))
                                                        "where_to_buy_es.pdf"
                                                     else
                                                        "where_to_buy_en.pdf"
        "links","enlaces" -> fileName = if (currentLocaleString.equals("es_ES"))
                                            "links_es.pdf"
                                        else
                                            "links_en.pdf"
        "about","sobre" -> fileName = if (currentLocaleString.equals("es_ES"))
                                            "about_es.pdf"
                                      else
                                            "about_en.pdf"
    }
    return fileName
}
