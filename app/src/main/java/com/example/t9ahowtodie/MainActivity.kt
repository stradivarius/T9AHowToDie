package com.example.t9ahowtodie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.ui.AttackStatsViewModel
import com.example.t9ahowtodie.ui.screens.AttackStats
import com.example.t9ahowtodie.ui.screens.Routes
import com.example.t9ahowtodie.ui.screens.SplashScreen
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            T9AHowToDieTheme {
                T9AHowToDie()
            }
        }
    }
}

@Composable
fun T9AHowToDie(attackStatsViewModel: AttackStatsViewModel = AttackStatsViewModel()) {
    val navHostController: NavHostController = rememberNavController()
    NavHost(navController = navHostController, startDestination = Routes.SPLASH_SCREEN){

        composable(route = Routes.SPLASH_SCREEN) {
            SplashScreen(navHostController)
        }

        composable(route = Routes.ATTACKS_STATS) {
            AttackStats(navHostController, attackStatsViewModel)
        }
    }
}
