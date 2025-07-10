package com.altankoc.raf.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.altankoc.raf.ui.theme.RafLight
import com.altankoc.raf.ui.theme.RafDark1
import androidx.compose.ui.graphics.Color
import com.altankoc.raf.ui.theme.PlusJakartaSans

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String
) {
    val items = listOf(
        Screen.ReadBooks,
        Screen.Favorites,
        Screen.AddBook,
        Screen.ReadingList
    )
    NavigationBar(
        containerColor = RafDark1
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        tint = if (selected) Color.White else RafLight
                    )
                },
                label = {
                    if (selected) Text(screen.label, color = Color.White, fontFamily = PlusJakartaSans)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
} 