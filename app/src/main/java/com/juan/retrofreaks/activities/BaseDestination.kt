package com.juan.retrofreaks.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.juan.retrofreaks.R
import com.juan.retrofreaks.repository.RouteViewModel

/**
*This file contains the logic to ba navigated as the default destination in the NavGraph.
*Also if there was to exist any problem in the destinations, it would be redirected to EmptyDestination()
*(This shouldn't even be possible)
 */
    @Composable
    fun BaseDestination(navController: NavController) {
        val routeViewmodel = RouteViewModel(navController.context)
        routeViewmodel.setRoute(BASE_SCREEN)
        EmptyDestination()
    }

    @Composable
    fun EmptyDestination() {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            ,painter = painterResource(id = R.drawable.main_background),
            contentDescription = "emptyImage", contentScale = ContentScale.FillBounds
        )
    }


