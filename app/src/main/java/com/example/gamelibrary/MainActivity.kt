package com.example.gamelibrary

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var games: MutableList<Game>
    private lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        games = GameStorage.load(this)
        adapter = GameAdapter(games)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val search = findViewById<SearchView>(R.id.searchView)
        val btnAZ = findViewById<Button>(R.id.btnAZ)
        val btnZA = findViewById<Button>(R.id.btnZA)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String?): Boolean {
                val filtered = games.filter {
                    it.title.contains(text ?: "", true)
                }.toMutableList()
                adapter.update(filtered)
                return true
            }

            override fun onQueryTextSubmit(query: String?) = false
        })

        btnAZ.setOnClickListener {
            games.sortBy { it.title }
            adapter.update(games)
        }

        btnZA.setOnClickListener {
            games.sortByDescending { it.title }
            adapter.update(games)
        }

        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Új játék hozzáadása")

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            val inputTitle = EditText(this)
            inputTitle.hint = "Játék címe"
            val inputPublisher = EditText(this)
            inputPublisher.hint = "Kiadó"
            val inputGenre = EditText(this)
            inputGenre.hint = "Műfaj"

            layout.addView(inputTitle)
            layout.addView(inputPublisher)
            layout.addView(inputGenre)
            layout.setPadding(50, 40, 50, 10)

            builder.setView(layout)

            builder.setPositiveButton("Hozzáadás") { dialog, _ ->
                val title = inputTitle.text.toString().ifEmpty { "Ismeretlen" }
                val publisher = inputPublisher.text.toString().ifEmpty { "Ismeretlen" }
                val genre = inputGenre.text.toString().ifEmpty { "Műfaj" }

                games.add(Game(title, publisher, genre, false))
                adapter.update(games)
                dialog.dismiss()
            }

            builder.setNegativeButton("Mégse") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    override fun onPause() {
        super.onPause()
        GameStorage.save(this, games)
    }
}
