package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.domain.model.BookStatus
import com.lifeforge.os.presentation.books.BooksViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun BooksScreen(viewModel: BooksViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.bookTitle) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text(strings.bookAuthor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.addBook(title, author)
                    title = ""
                    author = ""
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(strings.addBook)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.books.isEmpty() && !state.isLoading) item { Text(strings.noBooks) }
        items(state.books, key = { it.id }) { book ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text(book.title, style = MaterialTheme.typography.bodyLarge)
                            Text(book.author, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${strings.statusLabel}: ${book.status.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        Button(onClick = { viewModel.cycleStatus(book.id) }, modifier = Modifier.padding(start = 8.dp)) {
                            Text(when (book.status) {
                                BookStatus.WantToRead -> "▶"
                                BookStatus.Reading -> "✓"
                                BookStatus.Completed -> "↺"
                                BookStatus.Paused -> "▶"
                                BookStatus.Dropped -> "▶"
                            })
                        }
                        IconButton(onClick = { viewModel.deleteBook(book.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete)
                        }
                    }
                }
            }
        }
    }
}