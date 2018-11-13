package com.kotlin.tutorial.web.controller

import com.kotlin.tutorial.core.impl.ConcurrentTasksExecutor
import com.kotlin.tutorial.core.impl.MockTask
import com.kotlin.tutorial.web.dto.ApiResponseDTO
import com.kotlin.tutorial.web.dto.ErrorResponseDTO
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
    fun sequential(@RequestParam("task") taskDelaysInSeconds: IntArray): ApiResponseDTO {
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
        return ApiResponseDTO(watch.totalTimeSeconds)
    }

    @GetMapping("/concurrent")
    fun concurrent(@RequestParam("task") taskDelaysInSeconds: IntArray, @RequestParam("threads") numberOfConcurrentThreads: Int): ApiResponseDTO {
        val watch = StopWatch()
        watch.start()

        val delayedTasks = IntStream.of(*taskDelaysInSeconds)
                .mapToObj{
                    MockTask(it)
                }
                .collect(Collectors.toList())

        ConcurrentTasksExecutor(numberOfConcurrentThreads, delayedTasks).execute()

        watch.stop()
        return ApiResponseDTO(watch.totalTimeSeconds)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: IllegalArgumentException): ErrorResponseDTO {
        return ErrorResponseDTO(e.message)
    }
}
