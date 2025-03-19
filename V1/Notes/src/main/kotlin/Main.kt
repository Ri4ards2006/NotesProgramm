import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Datenklasse für eine Notiz
data class Note(val fileName: String, val title: String, val preview: String)

// Funktion zum Laden der Notizen aus dem Verzeichnis
fun loadNotes(notesDir: Path): List<Note> {
    return try {
        Files.list(notesDir).map { file ->
            val content = Files.readString(file)
            val lines = content.lines()
            val title = lines.firstOrNull() ?: ""
            val preview = lines.drop(1).joinToString(" ").take(50)
            Note(file.fileName.toString(), title, preview)
        }.toList()
    } catch (e: Exception) {
        emptyList() // Bei Fehlern eine leere Liste zurückgeben
    }
}

@Composable
fun App() {
    // Verzeichnis für Notizen im Home-Verzeichnis des Nutzers
    val notesDir = remember { Paths.get(System.getProperty("user.home"), ".notesapp") }
    if (!Files.exists(notesDir)) {
        Files.createDirectory(notesDir)
    }

    // Zustandsverwaltung
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var currentTitle by remember { mutableStateOf("") }
    var currentContent by remember { mutableStateOf("") }

    // Notizen beim Start laden
    LaunchedEffect(Unit) {
        notes = loadNotes(notesDir)
    }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Notizenliste (linke Spalte)
            Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
                Button(
                    onClick = {
                        val newFileName = "note_${System.currentTimeMillis()}.txt"
                        selectedNote = Note(newFileName, "", "")
                        currentTitle = ""
                        currentContent = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Neue Notiz")
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(notes) { note ->
                        Column(
                            modifier = Modifier
                                .clickable {
                                    selectedNote = note
                                    val content = Files.readString(notesDir.resolve(note.fileName))
                                    val lines = content.lines()
                                    currentTitle = lines.firstOrNull() ?: ""
                                    currentContent = lines.drop(1).joinToString("\n")
                                }
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(note.title, style = MaterialTheme.typography.h6)
                            Text(note.preview, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
            // Editor (rechte Spalte)
            Column(modifier = Modifier.weight(2f).fillMaxHeight().padding(8.dp)) {
                if (selectedNote != null) {
                    TextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        label = { Text("Titel") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = currentContent,
                        onValueChange = { currentContent = it },
                        label = { Text("Inhalt") },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val content = currentTitle + "\n" + currentContent
                            Files.writeString(notesDir.resolve(selectedNote!!.fileName), content)
                            val lines = content.lines()
                            val title = lines.firstOrNull() ?: ""
                            val preview = lines.drop(1).joinToString(" ").take(50)
                            val updatedNote = Note(selectedNote!!.fileName, title, preview)
                            notes = notes.filter { it.fileName != selectedNote!!.fileName } + updatedNote
                        }) {
                            Text("Speichern")
                        }
                        Button(onClick = {
                            Files.delete(notesDir.resolve(selectedNote!!.fileName))
                            notes = notes.filter { it.fileName != selectedNote!!.fileName }
                            selectedNote = null
                            currentTitle = ""
                            currentContent = ""
                        }) {
                            Text("Löschen")
                        }
                    }
                } else {
                    Text(
                        "Wähle eine Notiz aus oder erstelle eine neue",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Notes App") {
        App()
    }
}