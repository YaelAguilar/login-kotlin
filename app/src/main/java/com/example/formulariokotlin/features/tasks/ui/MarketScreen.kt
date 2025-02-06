@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.example.formulariokotlin.features.tasks.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.formulariokotlin.features.tasks.data.models.Task
import com.example.formulariokotlin.features.tasks.viewmodel.TaskUIState
import com.example.formulariokotlin.features.tasks.viewmodel.TaskViewModel
import com.example.formulariokotlin.ui.theme.DarkGray
import com.example.formulariokotlin.ui.theme.LightGray
import com.example.formulariokotlin.ui.theme.Orange

@Composable
fun MarketScreen(
    onLogout: () -> Unit,
    token: String,
    onOpenTextRecognition: () -> Unit,
    taskViewModel: TaskViewModel = viewModel()
) {
    val context = LocalContext.current
    val tasks by taskViewModel.tasks.collectAsState()
    val uiState by taskViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = token) {
        if (token.isNotEmpty()) {
            taskViewModel.loadTasks(token)
        } else {
            Toast.makeText(context, "Token no válido.", Toast.LENGTH_SHORT).show()
            onLogout()
        }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Market", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Orange,
                contentColor = Color.White
            ) {
                Text("+", style = MaterialTheme.typography.bodyLarge)
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = DarkGray
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { onOpenTextRecognition() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Escanear Texto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkGray)
                    .padding(paddingValues)
            ) {
                if (tasks.isEmpty()) {
                    Text(
                        text = "No hay tareas disponibles.",
                        color = LightGray,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks.size) { index ->
                            val task = tasks[index]
                            TaskCard(
                                task = task,
                                onClick = { showDetailDialog = task },
                                onLongClick = { showLongPressMenu = task }
                            )
                        }
                    }
                }

                when (uiState) {
                    is TaskUIState.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                    is TaskUIState.Success -> {
                        val msg = (uiState as TaskUIState.Success).message
                        LaunchedEffect(msg) {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            taskViewModel.resetUIState()
                        }
                    }
                    is TaskUIState.Error -> {
                        val errorMsg = (uiState as TaskUIState.Error).message
                        LaunchedEffect(errorMsg) {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            taskViewModel.resetUIState()
                        }
                    }
                    else -> {}
                }
            }
        }
    )

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

    if (showDetailDialog != null) {
        TaskDetailDialog(
            task = showDetailDialog!!,
            onDismiss = { showDetailDialog = null }
        )
    }

    if (showLongPressMenu != null) {
        TaskOptionsDialog(
            task = showLongPressMenu!!,
            onEdit = {
                editingTask = showLongPressMenu
                showLongPressMenu = null
            },
            onDelete = {
                showDeleteConfirm = showLongPressMenu
                showLongPressMenu = null
            },
            onDismiss = { showLongPressMenu = null }
        )
    }

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

    if (showDeleteConfirm != null) {
        DeleteConfirmationDialog(
            task = showDeleteConfirm!!,
            onConfirm = {
                onConfirmDelete(showDeleteConfirm!!.id)
                showDeleteConfirm = null
            },
            onDismiss = { showDeleteConfirm = null }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Orange.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1
            )
            Text(
                text = task.content,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                maxLines = 3
            )
        }
    }
}

@Composable
fun TaskDetailDialog(
    task: Task,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(task.title) },
        text = { Text(task.content) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun TaskOptionsDialog(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(task.title) },
        text = { Text("¿Qué acción deseas realizar?") },
        confirmButton = {
            TextButton(onClick = onEdit) {
                Text("Editar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    task: Task,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Tarea") },
        text = { Text("¿Estás seguro de eliminar '${task.title}'?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí, eliminar", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CreateOrEditTaskDialog(
    titleDialog: String,
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    var showEmptyFieldsToast by remember { mutableStateOf(false) }

    if (showEmptyFieldsToast) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Campos no pueden estar vacíos.", Toast.LENGTH_SHORT).show()
            showEmptyFieldsToast = false
        }
    }

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
                if (title.isNotBlank() && content.isNotBlank()) {
                    onConfirm(title, content)
                } else {
                    showEmptyFieldsToast = true
                }
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
