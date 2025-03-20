import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Farbpalette
val WoodyBrown = Color(0xFF6B4F31)
val CreamWhite = Color(0xFFFDF5E6)
val LightWood = Color(0xFFD2B48C)
val DarkWood = Color(0xFF4A2B32)

// Hintergrund-Gradient
val gradientBrush = Brush.linearGradient(
    colors = listOf(WoodyBrown.copy(alpha = 0.9f), DarkWood.copy(alpha = 0.9f)),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
)

// Farbschema
val colorScheme = lightColorScheme(
    primary = WoodyBrown,
    secondary = LightWood,
    background = CreamWhite,
    surface = CreamWhite,
    onPrimary = CreamWhite,
    onSecondary = WoodyBrown,
    onBackground = WoodyBrown,
    onSurface = WoodyBrown
)

@Composable
fun App(onClose: () -> Unit) {
    val notesDir = remember { Paths.get(System.getProperty("user.home"), ".notesapp") }
    if (!Files.exists(notesDir)) {
        Files.createDirectory(notesDir)
    }

    var notes by remember { mutableStateOf(listOf<Note>()) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var currentTitle by remember { mutableStateOf("") }
    var currentContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        notes = loadNotes(notesDir)
    }

    MaterialTheme(colorScheme = colorScheme) {
        Box(modifier = Modifier.fillMaxSize().background(brush = gradientBrush)) {
            Row {
                // Linke Spalte
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val newFileName = "note_${System.currentTimeMillis()}.txt"
                                selectedNote = Note(newFileName, "", "")
                                currentTitle = ""
                                currentContent = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WoodyBrown,
                                contentColor = CreamWhite
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Neue Notiz")
                        }

                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkWood,
                                contentColor = CreamWhite
                            )
                        ) {
                            Text("Exit")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(notes) { note ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = CreamWhite.copy(alpha = 0.9f)
                                )
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(LightWood)
                                    )
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            note.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = WoodyBrown
                                        )
                                        Text(
                                            note.preview,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = WoodyBrown.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Rechte Spalte
                Card(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CreamWhite.copy(alpha = 0.9f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (selectedNote != null) {
                            OutlinedTextField(
                                value = currentTitle,
                                onValueChange = { currentTitle = it },
                                label = { Text("Titel", color = WoodyBrown) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = WoodyBrown,
                                    unfocusedTextColor = WoodyBrown.copy(alpha = 0.8f),
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLabelColor = WoodyBrown,
                                    unfocusedLabelColor = WoodyBrown.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = currentContent,
                                onValueChange = { currentContent = it },
                                label = { Text("Inhalt", color = WoodyBrown) },
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = WoodyBrown,
                                    unfocusedTextColor = WoodyBrown.copy(alpha = 0.8f),
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLabelColor = WoodyBrown,
                                    unfocusedLabelColor = WoodyBrown.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val content = currentTitle + "\n" + currentContent
                                        Files.writeString(notesDir.resolve(selectedNote!!.fileName), content)
                                        val lines = content.lines()
                                        val title = lines.firstOrNull() ?: ""
                                        val preview = lines.drop(1).joinToString(" ").take(50)
                                        val updatedNote = Note(selectedNote!!.fileName, title, preview)
                                        notes = notes.filter { it.fileName != selectedNote!!.fileName } + updatedNote
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WoodyBrown,
                                        contentColor = CreamWhite
                                    )
                                ) {
                                    Text("Speichern")
                                }

                                Button(
                                    onClick = {
                                        Files.delete(notesDir.resolve(selectedNote!!.fileName))
                                        notes = notes.filter { it.fileName != selectedNote!!.fileName }
                                        selectedNote = null
                                        currentTitle = ""
                                        currentContent = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LightWood,
                                        contentColor = WoodyBrown
                                    )
                                ) {
                                    Text("Löschen")
                                }
                            }
                        } else {
                            Text(
                                "Wähle eine Notiz aus oder erstelle eine neue",
                                color = WoodyBrown,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Notes App") {
        App(onClose = ::exitApplication)
    }
}

data class Note(val fileName: String, val title: String, val preview: String)

fun loadNotes(notesDir: Path): List<Note> {
    return Files.list(notesDir).use { paths ->
        paths.filter { Files.isRegularFile(it) }
            .map { path ->
                val content = Files.readString(path)
                val lines = content.lines()
                val title = lines.firstOrNull() ?: ""
                val preview = lines.drop(1).joinToString(" ").take(50)
                Note(path.fileName.toString(), title, preview)
            }
            .toList()
    }
}