package com.example.dayplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DayPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val days = listOf(
        "Понедельник", "Вторник", "Среда",
        "Четверг", "Пятница", "Суббота", "Воскресенье"
    )

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val fragment = TaskDayFragment()
        fragment.arguments = Bundle().apply {
            putString("day", days[position])
        }
        return fragment
    }
}