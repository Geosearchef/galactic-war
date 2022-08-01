package tasks

import data.User
import util.Util.logger
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.function.Supplier

object TaskService {

    private val log = logger()

    private val workerThread = Thread(::process).apply { start() }
    private val taskQueue: Queue<Task<Any>> = LinkedList()
    private var stopRequested = false

    fun init() {
        log.info("Starting task service")
    }

    fun process() {
        log.info("Starting task service thread...")

        while(! synchronized(workerThread) { stopRequested }) {
            while(true) {
                val task: Task<Any>? = synchronized(taskQueue) { taskQueue.poll() }

                if(task != null) {
                    try {
                        if(task is RunnableTask<*>) {
                            task.runnable.run()
                            task.future.complete(null)
                        } else if(task is SupplierTask) {
                            val value: Any = task.supplier.get()
                            task.future.complete(value)
                        }
                    } catch(e: Exception) {
                        log.warn("Error while processing task from ${(task.source as? User?)?.username ?: "?"}", e)
                    }

                    log.info("Processed task, was queued for ${System.currentTimeMillis() - task.submissionTime} ms")
                    log.info("Queue length: ${taskQueue.size}") // would have to be synchronized
                } else {
                    break
                }
            }

            try { Thread.sleep(5) } catch(e: InterruptedException) {}
        }

        repeat(5) {
            log.error("TERMINATING TASK PROCESSOR THREAD")
        }
    }

    fun addTask(runnable: Runnable) = addTask(RunnableTask(null, CompletableFuture<Unit>(),  runnable))
    fun addTask(source: Any?, runnable: Runnable) = addTask(RunnableTask(source, CompletableFuture<Unit>(), runnable))
    fun <T: Any> addTask(supplier: Supplier<T>) = addTask(SupplierTask<T>(null, CompletableFuture<T>(), supplier))
    fun <T: Any> addTask(source: Any?, supplier: Supplier<T>) = addTask(SupplierTask<T>(source, CompletableFuture<T>(), supplier))
    fun <T: Any> addTask(task: Task<T>): Future<T> {
        synchronized(taskQueue) {
            taskQueue.add(task as Task<Any>)
            log.info("Task added, queue length: ${taskQueue.size}")

            if(taskQueue.size >= 100) {
                log.warn("Task queue backlog: ${taskQueue.size}")
            }
        }
        return task.future
    }

    fun requestStop() {
        synchronized(workerThread) {
            stopRequested = true
            workerThread.interrupt()
        }
    }

    fun verifyTaskThread() {
        if(Thread.currentThread() != workerThread) {
            log.error("Verification of task thread failed", RuntimeException())
            Thread.currentThread().stop()
        }
    }

    abstract class Task<T: Any>(val source: Any?, val future: CompletableFuture<T>, val submissionTime: Long = System.currentTimeMillis())
    class RunnableTask<T: Unit>(source: Any?, future: CompletableFuture<T>, val runnable: Runnable) : Task<T>(source, future)
    class SupplierTask<T: Any>(source: Any?, future: CompletableFuture<T>, val supplier: Supplier<T>) : Task<T>(source, future)
}