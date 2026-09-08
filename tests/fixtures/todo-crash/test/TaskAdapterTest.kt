package com.example.todo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TaskAdapterTest {

    private val adapter = TaskAdapter()

    @Test
    fun testFormatTaskWithNonNullDescription() {
        val task = TaskItem("1", "Buy Milk", "Whole milk 1L")
        val result = adapter.formatTaskDisplay(task)
        assertEquals("Buy Milk (Desc: Whole milk 1L, length: 13)", result)
    }

    @Test
    fun testFormatTaskWithNullDescription() {
        val task = TaskItem("2", "Read Book", null)
        val result = adapter.formatTaskDisplay(task)
        assertNotNull(result)
        assertEquals("Read Book (Desc: None, length: 0)", result)
    }
}
