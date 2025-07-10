package com.altankoc.raf.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object ReadBooks : Screen("read_books", Icons.Filled.Done, "Okuduklarım")
    object Favorites : Screen("favorites", Icons.Filled.Favorite, "Favoriler")
    object AddBook : Screen("add_book", Icons.Filled.Add, "Ekle")
    object ReadingList : Screen("reading_list", Icons.Filled.Menu, "Okunacaklar")
} 