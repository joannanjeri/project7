package com.example.project7

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * main activity of the app
 * initializes and manages the UI and firebase authentication state
 */
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var notesRecycler: RecyclerView
    private lateinit var addNoteButton: ImageButton
    private lateinit var authButton: ImageButton
    private lateinit var notesAdapter: NotesAdapter

    /**
     * onCreate is called when the activity is starting
     * it initializes the user interface and Firebase components
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        initializeUI()
        setupRecyclerView()
        setupAuthStateListener()
    }

    /**
     * onStart is called when the activity is becoming visible to the user
     * it attaches the authentication state listener to Firebase Auth
     */
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    /**
     * onStop is called when the activity is no longer visible to the user
     * it removes the authentication state listener from Firebase Auth
     */
    override fun onStop() {
        super.onStop()
        if (::authStateListener.isInitialized) {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    private fun initializeUI() {
        notesRecycler = findViewById(R.id.notesRecycler)
        addNoteButton = findViewById(R.id.addNoteButton)
        authButton = findViewById(R.id.authButton)

        addNoteButton.setOnClickListener {
            startActivity(Intent(this, NoteScreen::class.java))
        }

        authButton.setOnClickListener {
            if (auth.currentUser != null) {
                auth.signOut()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, UserScreen::class.java))
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesRecycler.layoutManager = layoutManager

        val onClick: (Note) -> Unit = { note ->
            val intent = Intent(this, NoteScreen::class.java).apply {
                putExtra("NOTE_ID", note.id.toString())
            }
            startActivity(intent)
        }

        val onDelete: (Note) -> Unit = { note ->
            showDeleteConfirmationDialog(note)
        }

        notesAdapter = NotesAdapter(onClick, onDelete)
        notesRecycler.adapter = notesAdapter
        loadNotes()
    }


    private fun setupAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(this, UserScreen::class.java)
                startActivity(intent)
                finish()
            } else {
                loadNotes()
            }
        }
    }


    private fun loadNotes() {
        val notesReference = FirebaseDatabase.getInstance().reference.child("notes")
        notesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notes = mutableListOf<Note>()
                for (snapshot in dataSnapshot.children) {
                    val note = snapshot.getValue(Note::class.java)
                    note?.let { notes.add(it) }
                }
                notesAdapter.submitList(notes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load notes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteNoteFromFirebase(note)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun deleteNoteFromFirebase(note: Note) {
        val notesReference = FirebaseDatabase.getInstance().reference.child("notes")
        notesReference.child(note.id.toString()).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                loadNotes()
            } else {
                Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
