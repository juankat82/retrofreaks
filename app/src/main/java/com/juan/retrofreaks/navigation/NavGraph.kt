package com.juan.retrofreaks.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.juan.retrofreaks.activities.*
import com.juan.retrofreaks.repository.RouteViewModel

/*
Creates all the possible destinations when clicking in DrawerLayout options
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SetupNavGraph (context: Context, navController: NavHostController) {
    val s by remember{ mutableStateOf(0) }
    s
    val t = remember{ mutableStateOf(0) }
    t

    //Registers the current route
    val routeViewModel = RouteViewModel(navController.context)
    //Creates the NavHost and configures is
    NavHost(navController = navController, startDestination = Screen.Home.getBaseScreen()) {
        /*
        Each composabla is one of the possible destinations. BaseDestination is the default.
         */
        composable(route = Screen.Home.getBaseScreen()){
            BaseDestination(navController = navController)
        }

        composable(
            route = Screen.Console.getBaseScreen()+"/{consoleId}/{actionName}",
            arguments = listOf(navArgument("consoleId") {type = NavType.StringType
                nullable = true},
            navArgument("actionName") {type = NavType.StringType
                nullable = true})
        ) { backstackEntry ->
            val formedRoute = CONSOLE_SCREEN+"/"+backstackEntry.arguments!!.getString("consoleId")!!+"/"+backstackEntry.arguments!!.getString("actionName")
            routeViewModel.setRoute(formedRoute)
            if (!routeViewModel.route.value.equals(routeViewModel.oldRoute.value))
                ConsoleDestination(
                    context,
                    navController = navController,
                    backstackEntry.arguments!!.getString("consoleId")!!
                )
        }
        
        composable(route = Screen.Misc.getBaseScreen()+"/{miscActionId}/"+MISC_EMPTY,
            arguments = listOf(navArgument("miscActionId") {type = NavType.StringType
                nullable = true},
                navArgument(MISC_EMPTY) {type = NavType.StringType
                    nullable = true})
            ){ backstackEntry ->
                val flashcarts_ode_name = if (backstackEntry.arguments!!.getString("miscActionId")!!.equals(
                        stringResource(id = com.juan.retrofreaks.R.string.flashcarts))) FLASHCARTS_ODE else
                            backstackEntry.arguments!!.getString("miscActionId")!!
                val formedRoute = MISC_SCREEN+"/"+flashcarts_ode_name+"/"+ MISC_EMPTY
                routeViewModel.setRoute(formedRoute)
                if (!routeViewModel.route.value.equals(routeViewModel.oldRoute.value))
                    MiscDestination(navController = navController)
            }
    }
}