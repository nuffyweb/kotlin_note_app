package notes.service

import persistence.Serializer
import note.model.Note
import utils.Utilities.isValidListIndex

class NotesService(serializerType: Serializer) {
    private var serializer: Serializer = serializerType
    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean {
        return notes.add(note)
    }

    fun deleteNote(indexToDelete: Int): Note? {
        return if (isValidListIndex(indexToDelete, notes)) {
            notes.removeAt(indexToDelete)
        } else null
    }

    fun updateNote(indexToUpdate: Int, note: Note?): Boolean {
        val foundNote = findNote(indexToUpdate)

        if ((foundNote != null) && (note != null)) {
            foundNote.title = note.title
            foundNote.priority = note.priority
            foundNote.category = note.category
            return true
        }

        return false
    }

    fun archiveNote(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val noteToArchive = notes[indexToArchive]
            if (!noteToArchive.isArchived) {
                noteToArchive.isArchived = true
                return true
            }
        }
        return false
    }

    fun listAllNotes(): String =
        if (notes.isEmpty()) "No notes stored"
        else formatListString(notes)

    fun listActiveNotes(): String =
        if (numberOfActiveNotes() == 0) "No active notes stored"
        else formatListString(notes.filter { note -> !note.isArchived })

    fun listArchivedNotes(): String =
        if (numberOfArchivedNotes() == 0) "No archived notes stored"
        else formatListString(notes.filter { note -> note.isArchived })

    fun listNotesBySelectedPriority(priority: Int): String {
        return if (notes.isEmpty()) {
            "No notes stored"
        } else {
            var listOfNotes = ""
            for (i in notes.indices) {
                if (notes[i].priority == priority) {
                    listOfNotes +=
                        """$i: ${notes[i]}
                        """.trimIndent()
                }
            }
            if (listOfNotes == "") {
                "No notes with priority: $priority"
            } else {
                "${numberOfNotesByPriority(priority)} notes with priority $priority: $listOfNotes"
            }
        }
    }

    fun numberOfNotes(): Int {
        return notes.size
    }

    fun numberOfArchivedNotes(): Int = notes.count { note: Note -> note.isArchived }
    fun numberOfActiveNotes(): Int = notes.count { note: Note -> !note.isArchived }
    fun numberOfNotesByPriority(priority: Int): Int = notes.count { note: Note -> note.priority == priority }

    fun findNote(index: Int): Note? {
        return if (isValidListIndex(index, notes)) {
            notes[index]
        } else null
    }

    fun isValidIndex(index: Int): Boolean {
        return isValidListIndex(index, notes)
    }

    fun searchByTitle(searchString: String) =
        formatListString(
            notes.filter { note -> note.title.contains(searchString, ignoreCase = true) }
        )

    private fun formatListString(notesToFormat: List<Note>): String =
        notesToFormat
            .joinToString(separator = "\n") { note ->
                notes.indexOf(note).toString() + ": " + note.toString()
            }

    @Throws(Exception::class)
    @Suppress("UNCHECKED_CAST")
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }
}