package com.example.todo

class TaskAdapter {
    fun formatTaskDisplay(task: TaskItem): String {
        val descText = task.description ?: "None"
        val descLength = task.description?.length ?: 0
        return "${task.title} (Desc: $descText, length: $descLength)"
    }
}
