package ru.lextop.miningpoolhub

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppExecutors(
    val diskIO: Executor,
    val networkIO: Executor,
    val mainThread: Executor,
    val workingThread: Executor
) {
     @Inject constructor() : this (
         diskIO = Executors.newSingleThreadExecutor(),
         networkIO = Executors.newFixedThreadPool(3),
         mainThread = MainThreadExecutor(),
         workingThread = Executors.newFixedThreadPool(3)
     )

    private class MainThreadExecutor : Executor {
        private val handler =  Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
}
