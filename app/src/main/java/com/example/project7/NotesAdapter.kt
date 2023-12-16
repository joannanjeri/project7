package com.example.project7


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * displays a list of notes in the main activity
 * it handles each note and the interactions with each item
 *
 * @property onClick this lambda function is called when an item in the list is clicked
 * @property onDelete this lambda function when the delete button of an item is clicked
 */

class NotesAdapter(
    private val onClick: (Note) -> Unit,
    private val onDelete: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private var notesList: List<Note> = emptyList()

    /**
     * this submits a new list to the adapter and sees the difference the old list and the new list
     *
     * @param notes this displays the new list of notes
     */

    fun submitList(notes: List<Note>) {
        val oldList = notesList
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            NotesDiffCallback(oldList, notes)
        )
        notesList = notes
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * this describes an item view and its place within the recycler view
     */

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvNoteTitle)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.deleteButton)

        /**
         * this binds the note to the view elements in each recycler view item
         *
         * @param note the note item that is bound in this view holder
         */

        fun bind(note: Note) {
            titleTextView.text = note.title

            itemView.setOnClickListener {
                onClick(note)
            }

            deleteImageView.setOnClickListener {
                onDelete(note)
            }
        }
    }

    /**
     * a class that calculates the diff between two lists
     *
     * @property oldList the old list of notes to compare
     * @property newList the new list of notes to compare
     */

    class NotesDiffCallback(
        private val oldList: List<Note>,
        private val newList: List<Note>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.bind(note)
    }
}

