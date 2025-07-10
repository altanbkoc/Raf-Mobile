package com.altankoc.raf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.altankoc.raf.ui.theme.RafTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.altankoc.raf.ui.BottomNavBar
import com.altankoc.raf.ui.NavGraph
import com.altankoc.raf.ui.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.compose.ui.platform.LocalContext
import com.altankoc.raf.roomdb.BookDatabase
import com.altankoc.raf.repository.BookRepository
import com.altankoc.raf.viewmodel.BookViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RafTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.ReadBooks.route

    val showBottomBar = currentRoute in listOf(
        Screen.ReadBooks.route,
        Screen.Favorites.route,
        Screen.AddBook.route,
        Screen.ReadingList.route
    )

    val context = LocalContext.current
    val db = BookDatabase.getDatabase(context)
    val repository = BookRepository(db.bookDao())
    val factory = BookViewModelFactory(repository)
    val viewModel: BookViewModel = viewModel(factory = factory)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navController = navController, viewModel = viewModel)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RafTheme {
        MainScreen()
    }
}