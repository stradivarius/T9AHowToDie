package com.example.t9ahowtodie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.ui.StatsViewModel
import com.example.t9ahowtodie.ui.components.BottomNavigationItem
import com.example.t9ahowtodie.ui.components.NavigationTextComponent
import com.example.t9ahowtodie.ui.screens.AttackStats
import com.example.t9ahowtodie.ui.screens.Routes
import com.example.t9ahowtodie.ui.screens.SplashScreen
import com.example.t9ahowtodie.ui.screens.TestStats
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            T9AHowToDieTheme {

                Surface (modifier = Modifier.fillMaxSize()) {

                    T9AHowToDie()

                }
            }
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun T9AHowToDie(attackStatsViewModel: StatsViewModel = StatsViewModel()) {
    val navHostController: NavHostController = rememberNavController()

    /* Live Data for hiding Bottom Nav Bar */
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    bottomBarState.value = navBackStackEntry?.destination?.route != Routes.SPLASH_SCREEN

    /* BOTTOM NAVIGATION BAR */
    val bottomNavigationItems = listOf<BottomNavigationItem>(
        BottomNavigationItem(
            title = "Attacks",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
            destination = Routes.ATTACKS_STATS
        ),
        BottomNavigationItem(
            title = "Tests",
            selectedIcon = Icons.Filled.CheckCircle,
            unselectedIcon = Icons.Outlined.CheckCircle,
            destination = Routes.TESTS_STATS
        ),
    )
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Scaffold (
        content = {
            Box (modifier = Modifier.padding(it)) {
                NavHost(
                    navController = navHostController,
                    startDestination = Routes.SPLASH_SCREEN
                ) {

                    composable(route = Routes.SPLASH_SCREEN) {
                        SplashScreen(navHostController)
                    }

                    composable(route = Routes.ATTACKS_STATS) {
                        AttackStats(navHostController, attackStatsViewModel)
                    }

                    composable(route = Routes.TESTS_STATS) {
                        TestStats(navHostController, attackStatsViewModel)
                    }
                }
            }
        },
        bottomBar = {
            if (bottomBarState.value)
                NavigationBar (
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    tonalElevation = 3.dp

                ) {
                    bottomNavigationItems.forEachIndexed{ index, item ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.secondary
                            ),
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navHostController.navigate(item.destination)
                            },
                            label = { NavigationTextComponent(text = item.title) },
                            icon = {
                                Icon(
                                    imageVector = if (selectedItemIndex == index) item.selectedIcon
                                        else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if (selectedItemIndex == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.secondary
                                )
                            }
                        )
                    }
                }
        }
    )
}

@Preview
@Composable
fun PreviewBegin() {
    T9AHowToDieTheme{
        T9AHowToDie(StatsViewModel())
    }
}