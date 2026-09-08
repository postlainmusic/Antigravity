package com.example.todo

data class TaskItem(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean = false
)
