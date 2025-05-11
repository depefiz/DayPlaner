package com.example.dayplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class TaskDayFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_day, container, false)
        taskListView = view.findViewById(R.id.taskListView2)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val day = arguments?.getString("day") ?: return
        val taskList = TaskStorage.loadTasks(requireContext())[day] ?: mutableListOf()

        taskAdapter = TaskAdapter(requireContext(), taskList)
        taskListView.adapter = taskAdapter
    }

    override fun onResume() {
        super.onResume()
        val day = arguments?.getString("day") ?: return
        val updatedList = TaskStorage.loadTasks(requireContext())[day] ?: mutableListOf()

        if (::taskAdapter.isInitialized) {
            taskAdapter.updateData(updatedList)
        }
    }
}

