package com.example.project7

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * provides firebase database functionalities
 * offers a method to get a reference to the notes node in the database
 */
class FirebaseRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    /**
     * gets a reference to the notes node in the firebase database
     * @return database reference to the notes node
     */
    fun getNotesReference(): DatabaseReference {
        return database.child("notes")
    }
}