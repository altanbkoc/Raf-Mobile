package com.altankoc.raf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.text.input.ImeAction
import com.altankoc.raf.ui.theme.*
import com.altankoc.raf.model.Book
import com.altankoc.raf.viewmodel.BookViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    viewModel: BookViewModel,
    bookId: Int
) {
    var book by remember { mutableStateOf<Book?>(null) }
    
    LaunchedEffect(bookId) {
        book = viewModel.getBookById(bookId)
    }
    
    if (book == null) {
        // Kitap bulunamadı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RafExtraLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Kitap bulunamadı",
                color = RafDark1,
                fontFamily = PlusJakartaSans,
                fontSize = 18.sp
            )
        }
        return
    }

    var title by remember { mutableStateOf(book!!.title) }
    var author by remember { mutableStateOf(book!!.author) }
    var pageCount by remember { mutableStateOf(book!!.pageCount.toString()) }
    var isRead by remember { mutableStateOf(book!!.isRead) }
    var isFavorite by remember { mutableStateOf(book!!.isFavorite) }
    var isEditing by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isFavoriteLoading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    fun capitalizeWords(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) {
                word.lowercase().replaceFirstChar { it.uppercase() }
            } else {
                word
            }
        }
    }

    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = tween(300),
        label = "favoriteScale"
    )

    LaunchedEffect(title, author, pageCount, isRead, isFavorite) {
        hasChanges = title != book!!.title || 
                    author != book!!.author || 
                    pageCount != book!!.pageCount.toString() ||
                    isRead != book!!.isRead ||
                    isFavorite != book!!.isFavorite
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RafExtraLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = RafDark1
                        )
                    }
                    Text(
                        text = book!!.title,
                        color = RafDark1,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        textAlign = TextAlign.Start
                    )
                    Row {
                        IconButton(
                            onClick = {
                                isFavoriteLoading = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(800)
                                    isFavorite = !isFavorite
                                    val updatedBook = book!!.copy(isFavorite = isFavorite)
                                    viewModel.updateBook(updatedBook)
                                    isFavoriteLoading = false
                                    
                                    val message = if (isFavorite) "Favorilere eklendi" else "Favorilerden çıkarıldı"
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isFavoriteLoading
                        ) {
                            if (isFavoriteLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = if (isFavorite) RafMain else RafDark1,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                                    tint = if (isFavorite) RafMain else RafDark1,
                                    modifier = Modifier.scale(favoriteScale)
                                )
                            }
                        }
                        IconButton(
                            onClick = { 
                                if (isEditing) {
                                    if (title.trim().isEmpty()) {
                                        Toast.makeText(context, "Kitap adı boş bırakılamaz", Toast.LENGTH_SHORT).show()
                                        return@IconButton
                                    }
                                    if (author.trim().isEmpty()) {
                                        Toast.makeText(context, "Yazar adı boş bırakılamaz", Toast.LENGTH_SHORT).show()
                                        return@IconButton
                                    }
                                    isSaving = true
                                    coroutineScope.launch {
                                        val formattedTitle = capitalizeWords(title.trim())
                                        val existing = viewModel.getBookById(book!!.id)
                                        if (formattedTitle.lowercase() != book!!.title.lowercase() && viewModel.isBookExists(formattedTitle)) {
                                            Toast.makeText(context, "Bu isimde başka bir kitap zaten var!", Toast.LENGTH_SHORT).show()
                                            isSaving = false
                                            return@launch
                                        }
                                        kotlinx.coroutines.delay(1200)
                                        val updatedBook = book!!.copy(
                                            title = formattedTitle,
                                            author = capitalizeWords(author.trim()),
                                            pageCount = pageCount.trim().toIntOrNull() ?: 0,
                                            isRead = isRead,
                                            isFavorite = isFavorite
                                        )
                                        viewModel.updateBook(updatedBook)
                                        isEditing = false
                                        hasChanges = false
                                        isSaving = false
                                        Toast.makeText(context, "Değişiklikler kaydedildi", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isEditing = true
                                }
                            },
                            enabled = (!isEditing || hasChanges) && !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = RafDark1,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                                    contentDescription = if (isEditing) "Kaydet" else "Düzenle",
                                    tint = if (!isEditing || hasChanges) RafDark1 else RafDark1.copy(alpha = 0.5f)
                                )
                            }
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Kitabı sil",
                                tint = RafDark2
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = RafExtraLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Kitap Adı",
                            color = RafDark1,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { 
                                if (isEditing) {
                                    title = it
                                }
                            },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isEditing) RafDark1 else RafDark1.copy(alpha = 0.7f),
                                fontFamily = PlusJakartaSans
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RafDark1,
                                unfocusedBorderColor = RafDark1.copy(alpha = 0.5f),
                                disabledBorderColor = RafDark1.copy(alpha = 0.3f),
                                focusedLabelColor = RafDark1,
                                unfocusedLabelColor = RafDark1.copy(alpha = 0.7f),
                                disabledLabelColor = RafDark1.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = RafExtraLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Yazar",
                            color = RafDark1,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = author,
                            onValueChange = { 
                                if (isEditing) {
                                    author = it
                                }
                            },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isEditing) RafDark1 else RafDark1.copy(alpha = 0.7f),
                                fontFamily = PlusJakartaSans
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RafDark1,
                                unfocusedBorderColor = RafDark1.copy(alpha = 0.5f),
                                disabledBorderColor = RafDark1.copy(alpha = 0.3f),
                                focusedLabelColor = RafDark1,
                                unfocusedLabelColor = RafDark1.copy(alpha = 0.7f),
                                disabledLabelColor = RafDark1.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = RafExtraLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Sayfa Sayısı",
                            color = RafDark1,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = pageCount,
                            onValueChange = { 
                                if (isEditing) {
                                    pageCount = it
                                }
                            },
                            enabled = isEditing,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = if (isEditing) RafDark1 else RafDark1.copy(alpha = 0.7f),
                                fontFamily = PlusJakartaSans
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RafDark1,
                                unfocusedBorderColor = RafDark1.copy(alpha = 0.5f),
                                disabledBorderColor = RafDark1.copy(alpha = 0.3f),
                                focusedLabelColor = RafDark1,
                                unfocusedLabelColor = RafDark1.copy(alpha = 0.7f),
                                disabledLabelColor = RafDark1.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = RafExtraLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Okuma Durumu",
                            color = RafDark1,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isRead,
                                onClick = { 
                                    if (isEditing) {
                                        isRead = true
                                        val updatedBook = book!!.copy(isRead = true)
                                        viewModel.updateBook(updatedBook)
                                    }
                                },
                                enabled = isEditing,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = RafDark1,
                                    unselectedColor = RafDark1.copy(alpha = 0.5f)
                                )
                            )
                            Text(
                                text = "Okundu",
                                color = if (isRead) RafMain else RafDark1.copy(alpha = 0.7f),
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !isRead,
                                onClick = { 
                                    if (isEditing) {
                                        isRead = false
                                        val updatedBook = book!!.copy(isRead = false)
                                        viewModel.updateBook(updatedBook)
                                    }
                                },
                                enabled = isEditing,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = RafDark1,
                                    unselectedColor = RafDark1.copy(alpha = 0.5f)
                                )
                            )
                            Text(
                                text = "Okunmadı",
                                color = if (!isRead) RafDark2 else RafDark1.copy(alpha = 0.7f),
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(
                "Kitabı Sil",
                color = RafDark1,
                fontFamily = PlusJakartaSans
            ) },
            text = { Text("Bu kitabı silmek istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        isLoading = true
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1500)
                            viewModel.deleteBook(book!!)
                            isLoading = false
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RafDark1, contentColor = RafExtraLight)
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
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