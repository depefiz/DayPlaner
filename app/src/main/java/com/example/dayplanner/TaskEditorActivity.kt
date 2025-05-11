package com.example.dayplanner

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class TaskEditorActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksByDay: MutableMap<String, MutableList<TaskItem>>
    private lateinit var selectedDayKey: String
    private lateinit var taskListView: ListView
    private val daysFull = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    private var currentDayIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_editor)

        tasksByDay = TaskStorage.loadTasks(this)

        val textDay = findViewById<TextView>(R.id.textCurrentDay)
        val btnPrev = findViewById<ImageView>(R.id.btnPrevDay)
        val btnNext = findViewById<ImageView>(R.id.btnNextDay)

        currentDayIndex = 0
        selectedDayKey = daysFull[currentDayIndex]
        textDay.text = selectedDayKey

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnPrev.setOnClickListener {
            currentDayIndex = (currentDayIndex + 6) % 7
            selectedDayKey = daysFull[currentDayIndex]
            textDay.text = selectedDayKey
            updateTaskList(selectedDayKey)
        }

        btnNext.setOnClickListener {
            currentDayIndex = (currentDayIndex + 1) % 7
            selectedDayKey = daysFull[currentDayIndex]
            textDay.text = selectedDayKey
            updateTaskList(selectedDayKey)
        }

        findViewById<Button>(R.id.addTaskButton).setOnClickListener {
            showAddTaskDialog()
        }

        taskListView = findViewById(R.id.taskListView2)
        taskAdapter = TaskAdapter(this, mutableListOf())
        taskListView.adapter = taskAdapter

        taskListView.setOnItemLongClickListener { parent, view, position, _ ->
            val taskToRemove = parent.getItemAtPosition(position) as? TaskItem
            if (taskToRemove != null) {
                AlertDialog.Builder(this)
                    .setTitle("Удаление задачи")
                    .setMessage("Вы действительно хотите удалить задачу:\n\n${taskToRemove.name}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        tasksByDay[selectedDayKey]?.remove(taskToRemove)
                        TaskStorage.saveTasks(this, tasksByDay)
                        updateTaskList(selectedDayKey)
                        val resultIntent = Intent()
                        resultIntent.putExtra("updated", true)
                        setResult(RESULT_OK, resultIntent)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            true
        }
        updateTaskList(selectedDayKey)
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        AlertDialog.Builder(this)
            .setTitle("Новая задача")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val start = dialogView.findViewById<EditText>(R.id.inputStartTime).text.toString()
                val end = dialogView.findViewById<EditText>(R.id.inputEndTime).text.toString()
                val name = dialogView.findViewById<EditText>(R.id.inputTaskName).text.toString()

                val duration = calculateDuration(start, end)
                val task = TaskItem(start, end, name, duration)

                val list = tasksByDay.getOrPut(selectedDayKey) { mutableListOf() }
                list.add(task)
                TaskStorage.saveTasks(this, tasksByDay)
                updateTaskList(selectedDayKey)

                TaskStorage.saveTasks(this, tasksByDay)

                updateTaskList(selectedDayKey)

                val resultIntent = Intent()
                resultIntent.putExtra("updated", true)
                setResult(RESULT_OK, resultIntent)
            }
            .setNegativeButton("Отмена", null)
            .show()

        TaskStorage.saveTasks(this, tasksByDay)

    }

    private fun updateTaskList(day: String) {
        val taskList = tasksByDay[day] ?: mutableListOf()
        taskAdapter.updateData(taskList)
    }

    private fun calculateDuration(start: String, end: String): String {
        try {
            val (startH, startM) = start.split(":").map { it.toInt() }
            val (endH, endM) = end.split(":").map { it.toInt() }

            val startTotal = startH * 60 + startM
            val endTotal = endH * 60 + endM
            val diff = endTotal - startTotal

            val hours = diff / 60
            val minutes = diff % 60
            return "${hours}ч ${minutes}м"
        } catch (e: Exception) {
            return "0ч 0м"
        }
    }
}
