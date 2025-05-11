package com.example.dayplanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TaskAdapter(private val context: Context, private val taskList: MutableList<TaskItem>) : BaseAdapter() {

    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Any = taskList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val task = taskList[position]

        view.findViewById<TextView>(R.id.textStartEndTime).text = "${task.start} – ${task.end}"
        view.findViewById<TextView>(R.id.textTaskName).text = task.name
        view.findViewById<TextView>(R.id.textDuration).text = task.duration

        return view
    }

    fun updateData(newList: List<TaskItem>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }
}

