package yzh.lifediary.util

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import yzh.lifediary.entity.User
import yzh.lifediary.view.TAG
import java.io.ObjectOutputStream
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object TaskExecutor {


    private const val CORE_POOL_SIZE = 0
    private val MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2
    private const val KEEP_ALIVE_SECONDS = 3L
    private val sThreadFactory = object : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "TaskPool:" + mCount.getAndIncrement())
        }
    }
    private val THREAD_POOL_EXECUTOR = ThreadPoolExecutor(
        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
        SynchronousQueue(), sThreadFactory
    )

    fun execute( runnable: Runnable){
        THREAD_POOL_EXECUTOR.execute(runnable)
    }

    fun  writeUserToFile(activity: Activity,user: User){
        val of = activity.openFileOutput(Constant.userFile, AppCompatActivity.MODE_PRIVATE)
        val oi =
            ObjectOutputStream(of)
        Log.d(TAG, user.toString())
        oi.writeObject(user)
        oi.flush()
        oi.close()
        of.close()
    }



}