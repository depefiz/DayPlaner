package com.example.dayplanner

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: ArrayList<TaskItem>
    private lateinit var listView: ListView
    private lateinit var tasksByDay: MutableMap<String, MutableList<TaskItem>>
    private lateinit var taskEditorLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val calendar = java.util.Calendar.getInstance()

        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayNames = listOf(
            "Воскресенье", "Понедельник", "Вторник", "Среда",
            "Четверг", "Пятница", "Суббота"
        )

        val dayIndex = when (dayOfWeek) {
            java.util.Calendar.MONDAY -> 1
            java.util.Calendar.TUESDAY -> 2
            java.util.Calendar.WEDNESDAY -> 3
            java.util.Calendar.THURSDAY -> 4
            java.util.Calendar.FRIDAY -> 5
            java.util.Calendar.SATURDAY -> 6
            java.util.Calendar.SUNDAY -> 0
            else -> 1
        }
        val currentDayName = dayNames[dayIndex]

        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // Месяцы с нуля
        val year = calendar.get(java.util.Calendar.YEAR)

        val fullDate = "$currentDayName, $day.$month.$year"


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tasksByDay = TaskStorage.loadTasks(this)

        findViewById<ImageView>(R.id.TaskEditorButton).setOnClickListener {
            val intent = Intent(this, TaskEditorActivity::class.java)
            taskEditorLauncher.launch(intent)
        }

        val textTodayDate = findViewById<TextView>(R.id.textTodayDate)
        textTodayDate.text = fullDate

        val tasksByDay = TaskStorage.loadTasks(this)
        val todayTasks = tasksByDay[currentDayName] ?: emptyList()

        listView = findViewById(R.id.TaskList)
        taskList = ArrayList(tasksByDay["Понедельник"] ?: emptyList())
        taskAdapter = TaskAdapter(this, taskList)
        listView.adapter = taskAdapter

        val taskAdapter = TaskAdapter(this, todayTasks.toMutableList())
        val listView = findViewById<ListView>(R.id.TaskList)
        listView.adapter = taskAdapter

        taskEditorLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data?.getBooleanExtra("updated", false) == true) {
                val tasksByDay = TaskStorage.loadTasks(this)
                val todayTasks = tasksByDay[currentDayName] ?: emptyList()
                taskAdapter.updateData(todayTasks)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tasksByDay = TaskStorage.loadTasks(this)
        val mondayTasks = tasksByDay["Понедельник"] ?: emptyList()
        taskList.clear()
        taskList.addAll(mondayTasks)
        taskAdapter.notifyDataSetChanged()
    }

    fun saveTasksToPrefs(context: Context, tasks: List<TaskItem>) {
        val prefs = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(tasks)
        prefs.edit().putString("task_list", json).apply()
    }

    private fun saveTasksToPreferences() {
        val sharedPref = getSharedPreferences("DayPlannerPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(taskList)
        editor.putString("task_list", json)
        editor.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val tasks = data?.getParcelableArrayListExtra<TaskItem>("tasks")
            if (tasks != null) {
                taskList.clear()
                taskList.addAll(tasks)
                taskAdapter.notifyDataSetChanged()
                saveTasksToPrefs(this, taskList)
            }
        }
        saveTasksToPreferences()
    }

}
