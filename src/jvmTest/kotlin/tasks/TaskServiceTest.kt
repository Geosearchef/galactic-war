package tasks

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskServiceTest {

    @Test
    fun testSingleRunnableTask() {
        Thread.sleep(500)

        val future = CompletableFuture<Unit>()

        TaskService.addTask {
            future.complete(null)
        }

        Thread.sleep(300)
        assertTrue(future.isDone)

        Thread.sleep(500)
    }

    @Test
    fun testSingleSupplierTask() {
        Thread.sleep(500)

        val res = TaskService.addTask<Int> {
            return@addTask 20
        }.get()

        assertEquals(20, res)

        Thread.sleep(500)
    }

    @Test
    fun testMultipleSupplierTasks() {
        val futures = ArrayList<Future<Int>>()

        val time = System.currentTimeMillis()

        for(i in 0 until 30) {
            futures.add(TaskService.addTask<Int> {
                Thread.sleep(100)
                println("Task completed: $i at ${System.currentTimeMillis() % 1000}")
                return@addTask i
            })
        }

        for(i in 0 until futures.size) {
            assertEquals(i, futures[i].get())
        }

        println("Took: " + (System.currentTimeMillis() - time).toFloat() / 1000.0f)
//        assertTrue(System.currentTimeMillis() - time < futures.size * 200)
    }
}