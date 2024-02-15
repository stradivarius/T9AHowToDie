package it.stradivarius.t9ahowtodie.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem (
    var title: String,
    var selectedIcon: ImageVector,
    var unselectedIcon: ImageVector,
    var destination: String
)