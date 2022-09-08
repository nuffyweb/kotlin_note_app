package menu

import notes.service.NotesService
import notes.service.NotesService.*
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import kotlin.system.exitProcess
import note.model.Note
import persistence.JSONSerializer
import java.io.File

private val NotesService = NotesService(JSONSerializer("notes.json"))
enum class MenuType(val code: Int) {
    AddNode(1),
    ListNotes(2),
    UpdateNote(3),
    DeleteNote(4),
    ArchiveNote(5),
    SearchNotes(6),
    Save(7),
    Load(8),
    GoMain(9),
    ExitApp(0)
}
class Menu {
    val red: String = "\u001b[31m"
    // Resets previous color codes
    val reset: String = "\u001b[0m"

    fun init() {
        runMenu()
    }
    private fun runMenu() {
        do {
            when (val option = mainMenu()) {
                MenuType.AddNode.code -> addNote()
                MenuType.ListNotes.code -> listNotes()
                MenuType.UpdateNote.code -> updateNote()
                MenuType.DeleteNote.code -> deleteNote()
                MenuType.ArchiveNote.code -> archiveNote()
                MenuType.SearchNotes.code -> searchNotes()
                MenuType.Save.code -> save()
                MenuType.Load.code -> load()
                MenuType.GoMain.code -> goMain()
                MenuType.ExitApp.code -> exitApp()
                else -> println("Invalid menu choice: $option")
            }
        } while (true)
    }

    private fun goMain() {
        runMenu()
    }

    private fun mainMenu(): Int {
        return readNextInt(
            """ 
         > ----------------------------------
         > |        SIMPLE NOTE APP         |
         > ----------------------------------
         > | MAIN NOTE MENU                 |
         > |   1) Add a note                |
         > |   2) List notes                |
         > |   3) Update a note             |
         > |   4) Delete a note             |
         > |   5) Archive a note            |
         > ----------------------------------
         > |   6) Search Notes              |
         > ----------------------------------
         > |   7) Save notes                |
         > |   8) Load notes                |
         > |   8) Go maim menu              |
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">")
        )
    }

    fun addNote() {
        // logger.info { "addNote() function invoked" }
        val noteTitle = readNextLine("Enter a title for the note: ")
        val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
        val noteCategory = readNextLine("Enter a category for the note: ")
        val isAdded = NotesService.add(Note(noteTitle, notePriority, noteCategory, false))

        if (isAdded) {
            println("Added Successfully")
        } else {
            println("Add Failed")
        }
    }

    fun listNotes() {
        if (NotesService.numberOfNotes() > 0) {
            val option = readNextInt(
                """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">")
            )

            when (option) {
                1 -> listAllNotes()
                2 -> listActiveNotes()
                3 -> listArchivedNotes()
                else -> println("Invalid option entered: $option")
            }
        } else {
            println("Option Invalid - No notes stored")
        }
    }

    fun listAllNotes() {
        println(NotesService.listAllNotes())
    }

    fun listActiveNotes() {
        println(NotesService.listActiveNotes())
    }

    fun listArchivedNotes() {
        println(NotesService.listArchivedNotes())
    }

    fun updateNote() {
        // logger.info { "updateNotes() function invoked" }
        listNotes()
        if (NotesService.numberOfNotes() > 0) {
            // only ask the user to choose the note if notes exist
            val indexToUpdate = readNextInt("Enter the index of the note to update: ")
            if (NotesService.isValidIndex(indexToUpdate)) {
                val noteTitle = readNextLine("Enter a title for the note: ")
                val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
                val noteCategory = readNextLine("Enter a category for the note: ")

                // pass the index of the note and the new note details to NotesService for updating and check for success.
                if (NotesService.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))) {
                    println("Update Successful")
                } else {
                    println("Update Failed")
                }
            } else {
                println("There are no notes for this index number")
            }
        }
    }

    fun deleteNote() {
        // logger.info { "deleteNotes() function invoked" }
        listNotes()
        if (NotesService.numberOfNotes() > 0) {
            // only ask the user to choose the note to delete if notes exist
            val indexToDelete = readNextInt("Enter the index of the note to delete: ")
            // pass the index of the note to NotesService for deleting and check for success.
            val noteToDelete = NotesService.deleteNote(indexToDelete)
            if (noteToDelete != null) {
                println("Delete Successful! Deleted note: ${noteToDelete.title}")
            } else {
                println("Delete NOT Successful")
            }
        }
    }

    fun archiveNote() {
        listActiveNotes()
        if (NotesService.numberOfActiveNotes() > 0) {
            // only ask the user to choose the note to archive if active notes exist
            val indexToArchive = readNextInt("Enter the index of the note to archive: ")
            // pass the index of the note to NotesService for archiving and check for success.
            if (NotesService.archiveNote(indexToArchive)) {
                println("Archive Successful!")
            } else {
                println("Archive NOT Successful")
            }
        }
    }

    fun searchNotes() {
        val searchTitle = readNextLine("Enter the description to search by: ")
        val searchResults = NotesService.searchByTitle(searchTitle)
        if (searchResults.isEmpty()) {
            println("No notes found")
        } else {
            println(searchResults)
        }
    }

    // ------------------------------------
// PERSISTENCE METHODS
// ------------------------------------
    fun save() {
        try {
            NotesService.store()
        } catch (e: Exception) {
            System.err.println("Error writing to file: $e")
        }
    }

    fun load() {
        try {
            NotesService.load()
        } catch (e: Exception) {
            System.err.println("Error reading from file: $e")
        }
    }

    fun exitApp() {
        println("Exiting...bye")
        exitProcess(0)
    }
}