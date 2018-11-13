package com.kotlin.tutorial.task.impl

import com.kotlin.tutorial.task.ITask
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import org.springframework.util.CollectionUtils
import java.util.*
import java.util.concurrent.Executors
import java.util.stream.Collectors


/**
 * Created by tony on 2018/11/13.
 */
class ConcurrentTasksExecutor(private val numberOfConcurrentThreads: Int, private val tasks: Collection<ITask>?) : ITask {

    val log = LoggerFactory.getLogger(this.javaClass)

    constructor(numberOfConcurrentThreads: Int, vararg tasks: ITask) : this(numberOfConcurrentThreads, if (tasks == null) null else Arrays.asList<ITask>(*tasks)) {}

    init {

        if (numberOfConcurrentThreads < 0) {
            throw RuntimeException("Amount of threads must be higher than zero.")
        }
    }

    /**
     * Converts collection of tasks (except null tasks) to collection of completable actions.
     * Each action will be executed in thread according to the scheduler created with [.createScheduler] method.
     *
     * @return list of completable actions
     */
    private val asConcurrentTasks: List<Completable>
        get() {

            if (tasks!=null) {

                val scheduler = createScheduler()

                return tasks.stream()
                        .filter { task -> task != null }
                        .map { task ->
                            Completable
                                    .fromAction {
                                        task.execute()
                                    }
                                    .subscribeOn(scheduler)
                        }
                        .collect(Collectors.toList())
            } else {

                return ArrayList<Completable>()
            }
        }

    /**
     * Checks whether tasks collection is empty.
     *
     * @return true if tasks collection is null or empty, false otherwise
     */
    private val isTasksCollectionEmpty: Boolean
        get() = CollectionUtils.isEmpty(tasks)


    /**
     * Executes all tasks concurrent way only if collection of tasks is not empty.
     * Method completes when all of the tasks complete (or one of them fails).
     * If one of the tasks failed the the exception will be rethrown so that it can be handled by mechanism that calls this method.
     */
    override fun execute() {

        if (isTasksCollectionEmpty) {
            log.warn("There are no tasks to be executed.")
            return
        }

        log.debug("Executing #{} tasks concurrent way.", tasks?.size)
        Completable.merge(asConcurrentTasks).blockingAwait()
    }

    /**
     * Creates a scheduler that will be used for executing tasks concurrent way.
     * Scheduler will use number of threads defined in [.numberOfConcurrentThreads]
     *
     * @return scheduler
     */
    private fun createScheduler() = Schedulers.from(Executors.newFixedThreadPool(numberOfConcurrentThreads))
}