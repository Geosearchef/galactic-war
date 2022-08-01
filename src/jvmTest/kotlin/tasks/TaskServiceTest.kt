package tasks

import util.ReflectionUtil
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.collections.ArrayList
import kotlin.test.*

class TaskServiceTest {

    init {
        TaskService.init()
    }

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
        val tasks = 30
        val sleepTime = 100L
        val futures = ArrayList<Future<Int>>()

        val time = System.currentTimeMillis()

        for(i in 0 until tasks) {
            futures.add(TaskService.addTask<Int> {
                Thread.sleep(sleepTime)
                println("Task completed: $i at ${System.currentTimeMillis() % 1000}")
                return@addTask i
            })
        }

        for(i in 0 until tasks) {
            assertEquals(i, futures[i].get())
        }

        println("Took: " + (System.currentTimeMillis() - time).toFloat() / 1000.0f)
//        assertTrue(System.currentTimeMillis() - time < futures.size * 200)
    }

    @Test
    fun testMultithreadedSubmission() {
        val threads = 10
        val tasksPerThread = 10

        val allThreadFutures = Array<Array<CompletableFuture<Unit>>>(threads) { Array(tasksPerThread) { CompletableFuture() } }

        val time = System.currentTimeMillis()

        for(i in 0 until threads) {
            Thread {
                val futures = ArrayList<Future<*>>()

                Thread.sleep((Math.random() * 500f).toLong())

                for(j in 0 until tasksPerThread / 2) {
                    futures.add(TaskService.addTask<Int> {
                        Thread.sleep(20)
                        allThreadFutures[i][j].complete(null)
                        return@addTask j * 10
                    })
                }

                for(j in tasksPerThread / 2 until tasksPerThread) {
                    futures.add(TaskService.addTask {
                        Thread.sleep(20)
                        allThreadFutures[i][j].complete(null)
                    })
                }

                for(i in 0 until tasksPerThread / 2) {
                    assertEquals(i * 10, futures[i].get())
                }
            }.start()
        }

        for(i in 0 until threads) {
            for(j in 0 until tasksPerThread) {
                allThreadFutures[i][j].join()
                assertFalse(allThreadFutures[i][j].isCancelled)
                assertFalse(allThreadFutures[i][j].isCompletedExceptionally)
                assertTrue(allThreadFutures[i][j].isDone)
            }
        }

        println("${allThreadFutures.flatMap { it.asIterable() }.filter { it.isDone }.count()} tasks are done, took: ${(System.currentTimeMillis() - time).toFloat() / 1000.0f}")
        assertTrue((ReflectionUtil.getFieldValue("taskQueue", TaskService) as? Queue<*>)?.isEmpty() ?: false)
    }
}