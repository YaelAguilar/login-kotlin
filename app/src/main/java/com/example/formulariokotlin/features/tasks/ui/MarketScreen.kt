@file:OptIn(ExperimentalFoundationApi::class)

package com.example.formulariokotlin.features.tasks.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.formulariokotlin.features.tasks.data.models.Task
import com.example.formulariokotlin.features.tasks.viewmodel.TaskUIState
import com.example.formulariokotlin.features.tasks.viewmodel.TaskViewModel
import com.example.formulariokotlin.ui.theme.DarkGray
import com.example.formulariokotlin.ui.theme.Orange

@Composable
fun MarketScreen(
    onLogout: () -> Unit,
    token: String,
    taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val uiState by taskViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        taskViewModel.loadTasks(token)
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf<Task?>(null) }
    var showLongPressMenu by remember { mutableStateOf<Task?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Task?>(null) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val onConfirmCreate: (String, String) -> Unit = { title, content ->
        taskViewModel.createTask(token, title, content)
    }
    val onConfirmEdit: (Int, String, String) -> Unit = { id, title, content ->
        taskViewModel.updateTask(token, id, title, content)
    }
    val onConfirmDelete: (Int) -> Unit = { id ->
        taskViewModel.deleteTask(token, id)
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkGray)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasks) { task ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .combinedClickable(
                            onClick = {
                                showDetailDialog = task
                            },
                            onLongClick = {
                                showLongPressMenu = task
                            }
                        )
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center).padding(4.dp)
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = Orange,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+")
        }
    }

    // Dialog de crear
    if (showCreateDialog) {
        CreateOrEditTaskDialog(
            titleDialog = "Nueva Tarea",
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, content ->
                onConfirmCreate(title, content)
                showCreateDialog = false
            }
        )
    }

    // Dialog de detalle
    if (showDetailDialog != null) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = null },
            title = { Text(showDetailDialog!!.title) },
            text = { Text(showDetailDialog!!.content) },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Menu al hacer long press
    if (showLongPressMenu != null) {
        AlertDialog(
            onDismissRequest = { showLongPressMenu = null },
            title = { Text(showLongPressMenu!!.title) },
            text = { Text("¿Qué acción deseas realizar?") },
            confirmButton = {
                TextButton(onClick = {
                    editingTask = showLongPressMenu
                    showLongPressMenu = null
                }) {
                    Text("Editar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirm = showLongPressMenu
                    showLongPressMenu = null
                }) {
                    Text("Eliminar")
                }
            }
        )
    }

    // Dialog para editar
    if (editingTask != null) {
        CreateOrEditTaskDialog(
            titleDialog = "Editar Tarea",
            initialTitle = editingTask!!.title,
            initialContent = editingTask!!.content,
            onDismiss = { editingTask = null },
            onConfirm = { newTitle, newContent ->
                onConfirmEdit(editingTask!!.id, newTitle, newContent)
                editingTask = null
            }
        )
    }

    // Confirmar eliminar
    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("¿Eliminar tarea?") },
            text = { Text("¿Estás seguro de eliminar '${showDeleteConfirm!!.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmDelete(showDeleteConfirm!!.id)
                    showDeleteConfirm = null
                }) {
                    Text("Sí, eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirm = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Manejo de estados (cargando, error, etc.)
    when (uiState) {
        is TaskUIState.Loading -> {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
        }
        is TaskUIState.Success -> {
            val msg = (uiState as TaskUIState.Success).message
            // Puedes mostrar un Toast o un snackbar
            LaunchedEffect(msg) {
                // e.g., Toast
                // Toast.makeText(LocalContext.current, msg, Toast.LENGTH_SHORT).show()
                taskViewModel.resetUIState()
            }
        }
        is TaskUIState.Error -> {
            val errorMsg = (uiState as TaskUIState.Error).message
            LaunchedEffect(errorMsg) {
                // Podrías mostrar un Toast
                // Toast.makeText(LocalContext.current, errorMsg, Toast.LENGTH_SHORT).show()
                taskViewModel.resetUIState()
            }
        }
        else -> {}
    }
}

/**
 * Reutilizable dialog para crear/editar tarea
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditTaskDialog(
    titleDialog: String,
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleDialog) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(title, content)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
