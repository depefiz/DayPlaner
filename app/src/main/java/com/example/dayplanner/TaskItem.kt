package com.example.dayplanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskItem(
    val start: String,
    val end: String,
    val name: String,
    val duration: String
) : Parcelable

val tasksByDay = mutableMapOf<String, ArrayList<TaskItem>>()