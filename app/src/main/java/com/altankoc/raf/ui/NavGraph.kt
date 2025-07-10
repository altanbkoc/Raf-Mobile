package com.altankoc.raf.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.altankoc.raf.viewmodel.BookViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: BookViewModel) {
    NavHost(navController, startDestination = Screen.ReadBooks.route) {
        composable(Screen.ReadBooks.route) { ReadBooksScreen(navController, viewModel) }
        composable(Screen.Favorites.route) { FavoriteBooksScreen(viewModel) }
        composable(Screen.AddBook.route) { AddBookScreen(navController, viewModel) }
        composable(Screen.ReadingList.route) { ReadingListScreen(viewModel) }
        composable("book_detail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
            BookDetailScreen(navController, viewModel, bookId)
        }
    }
} 