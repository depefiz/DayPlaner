package com.example.dayplanner

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskStorage {
    private const val PREFS_NAME = "DayPlannerPrefs"
    private const val TASKS_KEY = "tasks_by_day"

    fun saveTasks(context: Context, tasksByDay: Map<String, List<TaskItem>>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(tasksByDay)
        editor.putString(TASKS_KEY, json)
        editor.apply()
    }

    fun loadTasks(context: Context): MutableMap<String, MutableList<TaskItem>> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(TASKS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<Map<String, List<TaskItem>>>() {}.type
            val map: Map<String, List<TaskItem>> = Gson().fromJson(json, type)
            map.mapValues { it.value.toMutableList() }.toMutableMap()
        } else {
            mutableMapOf(
                "Понедельник" to mutableListOf(),
                "Вторник" to mutableListOf(),
                "Среда" to mutableListOf(),
                "Четверг" to mutableListOf(),
                "Пятница" to mutableListOf(),
                "Суббота" to mutableListOf(),
                "Воскресенье" to mutableListOf()
            )
        }
    }
}
