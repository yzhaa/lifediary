package yzh.lifediary.okhttp;



import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池没有写成静态的，所以创建多个实例的时候，会创建多个进程
 */

public class Dispatcher {


    private final int CORE_POOL_SIZE = 0;
    private final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors()*2;
    private  final int BACKUP_POOL_SIZE = 5;
    private  final int KEEP_ALIVE_SECONDS = 3;

    private  ThreadPoolExecutor sBackupExecutor;
    public  final Executor THREAD_POOL_EXECUTOR;

    private  final ThreadFactory sThreadFactory ;

    public Dispatcher() {
        sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "MyOkhttp:" + mCount.getAndIncrement());
            }
        };

        THREAD_POOL_EXECUTOR=      new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new SynchronousQueue<>(), sThreadFactory);;

    }

    /**
     * 丢弃策略，将多余的任务新建个备份线程池来执行
     */
    private final RejectedExecutionHandler sRunOnSerialPolicy =
            new RejectedExecutionHandler() {
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    synchronized (this) {
                        if (sBackupExecutor == null) {
                            LinkedBlockingQueue<Runnable> sBackupExecutorQueue = new LinkedBlockingQueue<>();
                            sBackupExecutor = new ThreadPoolExecutor(
                                    BACKUP_POOL_SIZE, BACKUP_POOL_SIZE, KEEP_ALIVE_SECONDS,
                                    TimeUnit.SECONDS, sBackupExecutorQueue, sThreadFactory);
                            sBackupExecutor.allowCoreThreadTimeOut(true);
                        }
                    }
                    sBackupExecutor.execute(r);
                }
            };





    public void enqueue(RealCall.AsyncCall call) {
        THREAD_POOL_EXECUTOR.execute(call);
    }



}