package com.kotlin.tutorial.web.controller

import com.kotlin.tutorial.task.impl.ConcurrentTasksExecutor
import com.kotlin.tutorial.task.impl.MockTask
import com.kotlin.tutorial.web.dto.ApiResponse
import com.kotlin.tutorial.web.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * Created by tony on 2018/11/13.
 */
@RestController
@RequestMapping("/tasks")
class TasksController {

    @GetMapping("/sequential")
    fun sequential(@RequestParam("task") taskDelaysInSeconds: IntArray): ApiResponse {

        val watch = StopWatch()
        watch.start()

        IntStream.of(*taskDelaysInSeconds)
                .mapToObj{
                    MockTask(it)
                }
                .forEach{
                    it.execute()
                }

        watch.stop()
        return ApiResponse(watch.totalTimeSeconds)
    }

    @GetMapping("/concurrent")
    fun concurrent(@RequestParam("task") taskDelaysInSeconds: IntArray, @RequestParam("threads",required = false,defaultValue = "1") numberOfConcurrentThreads: Int): ApiResponse {

        val watch = StopWatch()
        watch.start()

        val delayedTasks = IntStream.of(*taskDelaysInSeconds)
                .mapToObj{
                    MockTask(it)
                }
                .collect(Collectors.toList())

        ConcurrentTasksExecutor(numberOfConcurrentThreads, delayedTasks).execute()

        watch.stop()
        return ApiResponse(watch.totalTimeSeconds)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: IllegalArgumentException) = ErrorResponse(e.message)
}
