package com.altankoc.raf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.raf.ui.theme.*
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBooksScreen(viewModel: BookViewModel) {
    var searchText by remember { mutableStateOf("") }
    val favoriteBooks = viewModel.favoriteBooks.collectAsState().value
    val filteredBooks = if (searchText.isBlank()) {
        favoriteBooks
    } else {
        favoriteBooks.filter { book ->
            book.title.contains(searchText, ignoreCase = true) ||
            book.author.contains(searchText, ignoreCase = true)
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<com.altankoc.raf.model.Book?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RafExtraLight)
    ) {
        Column {
            Text(
                text = "Favorilerim",
                color = RafDark1,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp),
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Kitap veya yazar ara...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Ara",
                        tint = RafDark1
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Temizle",
                                tint = RafDark1
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Search
                ),
                textStyle = androidx.compose.ui.text.TextStyle(color = RafDark1),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RafDark1,
                    unfocusedBorderColor = RafDark1.copy(alpha = 0.5f),
                    focusedLabelColor = RafDark1,
                    unfocusedLabelColor = RafDark1.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "${filteredBooks.size} sonuc bulundu",
                color = RafDark1,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredBooks) { book ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 80.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(RafDark2, RafMain)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedBook = book
                                showDialog = true
                            },
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = book.title,
                                    color = RafExtraLight,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    fontFamily = PlusJakartaSans,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start,
                                    maxLines = 2,
                                    softWrap = true
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "Favori",
                                    tint = RafExtraLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = book.author,
                                    color = RafExtraLight.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = PlusJakartaSans,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = "${book.pageCount} sayfa",
                                    color = RafExtraLight.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = PlusJakartaSans,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(start = 8.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    if (showDialog && selectedBook != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Favoriden Kaldır",
                    color = RafDark1,
                    fontFamily = PlusJakartaSans
                )
            },
            text = {
                Text("Bunu favorilerden kaldırmak istediğinize emin misiniz?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        isLoading = true
                        coroutineScope.launch {
                            delay(1500)
                            val updatedBook = selectedBook!!.copy(isFavorite = false)
                            viewModel.updateBook(updatedBook)
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RafDark1, contentColor = RafExtraLight)
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RafDark1, contentColor = RafExtraLight)
                ) {
                    Text("Hayır")
                }
            }
        )
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000))
                .zIndex(2f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = RafDark1)
        }
    }
} 