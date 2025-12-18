package com.example.gamelibrary

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GameStorage {
    private const val PREF = "games"
    private const val KEY = "list"

    fun save(context: Context, games: List<Game>) {
        val json = Gson().toJson(games)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(KEY, json).apply()
    }

    fun load(context: Context): MutableList<Game> {
        val json = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return mutableListOf()

        val type = object : TypeToken<List<Game>>() {}.type
        return Gson().fromJson(json, type)
    }
}
