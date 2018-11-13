package com.kotlin.tutorial.task.impl

import java.util.concurrent.TimeUnit
import com.kotlin.tutorial.task.ITask

/**
 * Created by tony on 2018/11/13.
 */
class MockTask(private val delayInSeconds: Int) : ITask {

    /**
     * Stores information if task was started.
     */
    var started: Boolean = false

    /**
     * Stores information if task was successfully finished.
     */
    var finishedSuccessfully: Boolean = false

    /**
     * Stores information if the task was interrupted.
     * It can happen if the thread that is running this task was killed.
     */
    var interrupted: Boolean = false

    /**
     * Stores the thread identifier in which the task was executed.
     */
    var threadId: Long = 0

    override fun execute() {
        try {
            this.threadId = Thread.currentThread().id
            this.started = true
            TimeUnit.SECONDS.sleep(delayInSeconds.toLong())
            this.finishedSuccessfully = true
        } catch (e: InterruptedException) {
            this.interrupted = true
        }

    }
}