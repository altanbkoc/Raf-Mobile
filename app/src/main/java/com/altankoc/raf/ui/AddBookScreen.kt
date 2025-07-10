package com.altankoc.raf.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.altankoc.raf.ui.theme.RafDark1
import com.altankoc.raf.ui.theme.RafExtraLight
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.altankoc.raf.model.Book
import androidx.compose.ui.graphics.Brush
import com.altankoc.raf.ui.theme.RafMain
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.material3.CircularProgressIndicator
import com.altankoc.raf.ui.theme.PlusJakartaSans
import kotlinx.coroutines.delay




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(navController: NavController, viewModel: BookViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var isRead by remember { mutableStateOf(true) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun formatName(input: String): String =
        input.trim().split(" ").filter { it.isNotBlank() }.joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RafExtraLight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Yeni Kitap Ekle",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
                color = RafDark1,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = title,
                onValueChange = {
                    if (it.length <= 30) title = it
                },
                label = { Text("Kitap Adı") },
                singleLine = true,
                isError = showError && title.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(color = RafDark1),
                trailingIcon = {
                    if (title.isNotEmpty()) {
                        IconButton(onClick = { title = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle", tint = RafDark1)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = author,
                onValueChange = {
                    if (it.length <= 30) author = it
                },
                label = { Text("Yazar Adı") },
                singleLine = true,
                isError = showError && author.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(color = RafDark1),
                trailingIcon = {
                    if (author.isNotEmpty()) {
                        IconButton(onClick = { author = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle", tint = RafDark1)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pageCount,
                onValueChange = {
                    if (it.length <= 5 && it.all { ch -> ch.isDigit() }) pageCount = it
                },
                label = { Text("Sayfa Sayısı") },
                singleLine = true,
                isError = false,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
                textStyle = LocalTextStyle.current.copy(color = RafDark1),
                trailingIcon = {
                    if (pageCount.isNotEmpty()) {
                        IconButton(onClick = { pageCount = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle", tint = RafDark1)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isRead,
                    onClick = { isRead = true },
                    colors = RadioButtonDefaults.colors(selectedColor = RafDark1)
                )
                Text("Okudum", color = RafDark1, fontFamily = PlusJakartaSans)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !isRead,
                    onClick = { isRead = false },
                    colors = RadioButtonDefaults.colors(selectedColor = RafDark1)
                )
                Text("Okumadım", color = RafDark1, fontFamily = PlusJakartaSans)
            }
            if (showError && errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(RafDark1, RafMain)),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(vertical = 0.dp)
            ) {
                Button(
                    onClick = {
                        showError = false
                        errorMessage = ""
                        if (title.isBlank() || author.isBlank()) {
                            showError = true
                            errorMessage = "Kitap adı ve yazar boş bırakılamaz!"
                            return@Button
                        }
                        val formattedTitle = formatName(title)
                        val formattedAuthor = formatName(author)
                        val page = pageCount.toIntOrNull() ?: 0
                        coroutineScope.launch {
                            isLoading = true
                            val exists = viewModel.isBookExists(formattedTitle)
                            if (exists) {
                                Toast.makeText(context, "Bu kitap zaten var", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            } else {
                                delay(1500)
                                viewModel.insertBook(
                                    Book(
                                        title = formattedTitle,
                                        author = formattedAuthor,
                                        pageCount = page,
                                        isRead = isRead
                                    )
                                )
                                isLoading = false
                                title = ""
                                author = ""
                                pageCount = ""
                                isRead = true
                                Toast.makeText(context, "Kitap eklendi!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = RafExtraLight,
                        disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        disabledContentColor = RafExtraLight.copy(alpha = 0.5f)
                    ),
                    elevation = null
                ) {
                    Text("Kaydet", fontWeight = FontWeight.Light, color = RafExtraLight, fontSize = 20.sp, fontFamily = PlusJakartaSans)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
} 