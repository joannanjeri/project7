package com.example.project7

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * activity for managing individual notes
 * handles creation, editing, and deletion of notes
 */
class NoteScreen : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: ImageButton

    private var noteId: String? = null

    /**
     * onCreate is called when the activity is starting
     * it initializes the user interface and sets up listeners for buttons
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_screen)

        titleEditText = findViewById(R.id.title)
        descriptionEditText = findViewById(R.id.description)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        noteId = intent.getStringExtra("NOTE_ID")

        if (noteId != null) {
            loadNoteDetails(noteId!!)
        }

        saveButton.setOnClickListener {
            saveNote()
        }

        deleteButton = findViewById<ImageButton?>(R.id.deleteButton).apply {
            if (noteId == null) {
                visibility = Button.GONE
            } else {
                visibility = Button.VISIBLE
                setOnClickListener {
                    confirmAndDeleteNote()
                }
            }
        }
    }

    private fun loadNoteDetails(noteId: String) {
        val notesReference = FirebaseDatabase.getInstance().reference.child("notes").child(noteId)
        notesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val note = dataSnapshot.getValue(Note::class.java)
                note?.let {
                    titleEditText.setText(it.title)
                    descriptionEditText.setText(it.description)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NoteScreen, "Failed to load note", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val isNewNote = noteId == null

        val note = Note(title = title, description = description)

        val firebaseRepository = FirebaseRepository()
        val notesReference = firebaseRepository.getNotesReference()

        if (isNewNote) {
            notesReference.push().setValue(note)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            notesReference.child(noteId!!).setValue(note)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun confirmAndDeleteNote() {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Note")
            setMessage("Are you sure you want to delete this note")
            setPositiveButton("Delete") { dialog, _ ->
                noteId?.let {
                    FirebaseDatabase.getInstance().reference.child("notes").child(it).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(context, "Failed to delete the note", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

}