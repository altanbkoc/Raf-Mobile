package com.altankoc.raf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import com.altankoc.raf.ui.theme.RafExtraLight
import com.altankoc.raf.ui.theme.RafDark1
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Brush
import com.altankoc.raf.ui.theme.RafMain
import com.altankoc.raf.ui.theme.RafDark2
import com.altankoc.raf.ui.theme.PlusJakartaSans
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadBooksScreen(navController: NavController, viewModel: BookViewModel) {
    var searchText by remember { mutableStateOf("") }
    val readBooks = viewModel.readBooks.collectAsState().value
    val filteredBooks = if (searchText.isBlank()) {
        readBooks
    } else {
        readBooks.filter { book ->
            book.title.contains(searchText, ignoreCase = true) ||
            book.author.contains(searchText, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RafExtraLight)
    ) {
        Column {
            Text(
                text = "Okuduklarım",
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
                keyboardOptions = KeyboardOptions(
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBooks) { book ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(RafMain, RafDark2)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                navController.navigate("book_detail/${book.id}")
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = book.title,
                                color = RafExtraLight,
                                fontWeight = FontWeight.Normal,
                                fontSize = 19.sp,
                                fontFamily = PlusJakartaSans,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = book.author,
                                    color = RafExtraLight.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = PlusJakartaSans,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = "${book.pageCount} sayfa",
                                    color = RafExtraLight.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = PlusJakartaSans
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 