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
import androidx.compose.ui.res.painterResource // Für Icons aus dem resources-Verzeichnis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Farbpalette
val DarkText = Color(0xFF2D1A12)    // Dunkleres Braun für Text
val CreamWhite = Color(0xFFFDF5E6)
val LightWood = Color(0xFFD2B48C)
val DarkWood = Color(0xFF4A2B32)
val WoodyBrown = Color(0xFF6B4F31)

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
    onSecondary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText
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
            Row(modifier = Modifier.padding(8.dp)) {
                // Linke Spalte
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Button-Leiste mit Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                            Icon(
                                painter = painterResource("icons/add.png"), // Eigenes Icon
                                contentDescription = "Neu",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Neue Notiz")
                        }

                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkWood,
                                contentColor = CreamWhite
                            )
                        ) {
                            Icon(
                                painter = painterResource("icons/close.png"), // Eigenes Icon
                                contentDescription = "Beenden",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exit")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Notizen-Liste
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
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource("icons/notes.png"), // Eigenes Icon
                                            contentDescription = "Notiz",
                                            tint = DarkText.copy(alpha = 0.6f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                note.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = DarkText
                                            )
                                            Text(
                                                note.preview,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DarkText.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Rechte Spalte
                Card(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = CreamWhite.copy(alpha = 0.9f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (selectedNote != null) {
                            OutlinedTextField(
                                value = currentTitle,
                                onValueChange = { currentTitle = it },
                                label = { Text("Titel", color = DarkText) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource("icons/title.png"), // Eigenes Icon
                                        contentDescription = "Titel",
                                        tint = DarkText.copy(alpha = 0.6f)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = DarkText,
                                    unfocusedTextColor = DarkText.copy(alpha = 0.8f),
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLabelColor = DarkText,
                                    unfocusedLabelColor = DarkText.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = currentContent,
                                onValueChange = { currentContent = it },
                                label = { Text("Inhalt", color = DarkText) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource("icons/edit.png"), // Eigenes Icon
                                        contentDescription = "Inhalt",
                                        tint = DarkText.copy(alpha = 0.6f)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = DarkText,
                                    unfocusedTextColor = DarkText.copy(alpha = 0.8f),
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLabelColor = DarkText,
                                    unfocusedLabelColor = DarkText.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource("icons/save.png"), // Eigenes Icon
                                        contentDescription = "Speichern",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
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
                                        contentColor = DarkText
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource("icons/delete.png"), // Eigenes Icon
                                        contentDescription = "Löschen",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Löschen")
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource("icons/notes.png"), // Eigenes Icon
                                    contentDescription = "Notizen",
                                    tint = DarkText.copy(alpha = 0.4f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Wähle eine Notiz aus oder erstelle eine neue",
                                    color = DarkText.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Notes App",
        resizable = true
    ) {
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