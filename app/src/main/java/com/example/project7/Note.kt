package com.example.project7

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * data class representing a note
 * used as an entity in room database and for firebase data structure
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val description: String = ""
) {
    constructor(): this(0, "", "")
}
