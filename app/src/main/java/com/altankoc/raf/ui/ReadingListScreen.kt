package com.altankoc.raf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.shape.RoundedCornerShape
import com.altankoc.raf.ui.theme.RafExtraLight
import com.altankoc.raf.ui.theme.RafMain
import com.altankoc.raf.ui.theme.RafDark2
import com.altankoc.raf.ui.theme.RafDark1
import com.altankoc.raf.ui.theme.PlusJakartaSans
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingListScreen(viewModel: BookViewModel) {
    var searchText by remember { mutableStateOf("") }
    val readingList = viewModel.unreadBooks.collectAsState().value
    val filteredBooks = if (searchText.isBlank()) {
        readingList
    } else {
        readingList.filter { book ->
            book.title.contains(searchText, ignoreCase = true) ||
            book.author.contains(searchText, ignoreCase = true)
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("") }
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
                text = "Okuma Listem",
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
                                brush = Brush.horizontalGradient(listOf(RafDark2, RafMain)),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth()
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
                                    fontSize = 19.sp,
                                    fontFamily = PlusJakartaSans,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Row {
                                    IconButton(onClick = {
                                        selectedBook = book
                                        dialogType = "delete"
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Kaldır", tint = Color.White)
                                    }
                                    IconButton(onClick = {
                                        selectedBook = book
                                        dialogType = "done"
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Filled.Done, contentDescription = "Okundu", tint = Color.White)
                                    }
                                }
                            }
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
    if (showDialog && selectedBook != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(
                if (dialogType == "delete") "Kitabı Sil" else "Okundu Olarak İşaretle",
                color = RafDark1,
                fontFamily = PlusJakartaSans
            ) },
            text = { Text(if (dialogType == "delete") "Bu kitabı kaldırmak istediğinize emin misiniz?" else "Bu kitabı okundu olarak işaretlemek istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        isLoading = true
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1500)
                            if (dialogType == "delete") {
                                viewModel.deleteBook(selectedBook!!)
                            } else if (dialogType == "done") {
                                val updatedBook = selectedBook!!.copy(isRead = true)
                                viewModel.updateBook(updatedBook)
                            }
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