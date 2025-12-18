package com.example.gamelibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(
    private var games: MutableList<Game>
) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val publisher: TextView = view.findViewById(R.id.tvPublisher)
        val genre: TextView = view.findViewById(R.id.tvGenre)
        val playing: CheckBox = view.findViewById(R.id.cbPlaying)
        val statusText: TextView = view.findViewById(R.id.tvRecommendedStatus)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDelete)
        val editButton: ImageButton = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.title.text = game.title
        holder.publisher.text = "Kiadó: ${game.publisher}"
        holder.genre.text = "Műfaj: ${game.genre}"
        holder.playing.isChecked = game.recommended

        // Frissítjük a státuszt a megfelelő színnel
        if (!holder.playing.isChecked) {
            if (game.recommended) {
                holder.statusText.text = "Ajánlom"
                holder.statusText.setTextColor(0xFF00AA00.toInt()) // zöld
            } else {
                holder.statusText.text = "Nem ajánlom"
                holder.statusText.setTextColor(0xFFDD0000.toInt()) // piros
            }
        } else {
            holder.statusText.text = ""
        }

        // “Még játszom” checkbox logika
        holder.playing.setOnCheckedChangeListener { _, checked ->
            game.recommended = checked
            if (!checked) {
                // Felugró lehetőség: Ajánlom/Nem ajánlom
                val options = arrayOf("Ajánlom", "Nem ajánlom")
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Beállítás")
                    .setItems(options) { dialog, which ->
                        if (which == 0) {
                            holder.statusText.text = "Ajánlom"
                            holder.statusText.setTextColor(0xFF00AA00.toInt())
                        } else {
                            holder.statusText.text = "Nem ajánlom"
                            holder.statusText.setTextColor(0xFFDD0000.toInt())
                        }
                        dialog.dismiss()
                    }.show()
            } else {
                holder.statusText.text = ""
            }
        }

        // Törlés
        holder.deleteButton.setOnClickListener {
            games.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, games.size)
        }

        // Módosítás
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            val inputTitle = EditText(context)
            inputTitle.hint = "Játék címe"
            inputTitle.setText(game.title)
            val inputPublisher = EditText(context)
            inputPublisher.hint = "Kiadó"
            inputPublisher.setText(game.publisher)
            val inputGenre = EditText(context)
            inputGenre.hint = "Műfaj"
            inputGenre.setText(game.genre)

            layout.addView(inputTitle)
            layout.addView(inputPublisher)
            layout.addView(inputGenre)
            layout.setPadding(50, 40, 50, 10)

            AlertDialog.Builder(context)
                .setTitle("Játék módosítása")
                .setView(layout)
                .setPositiveButton("Mentés") { dialog, _ ->
                    game.title = inputTitle.text.toString()
                    game.publisher = inputPublisher.text.toString()
                    game.genre = inputGenre.text.toString()
                    notifyItemChanged(position)
                    dialog.dismiss()
                }
                .setNegativeButton("Mégse") { dialog, _ -> dialog.cancel() }
                .show()
        }
    }

    override fun getItemCount() = games.size

    fun update(newList: MutableList<Game>) {
        games = newList
        notifyDataSetChanged()
    }
}
