package chapter8;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: XINGHAIFANG
 * Date: 2019/7/26
 * Time: 9:57
 *  增加日志和计时等功能的线程池;
 */
public class TimingThreadPool extends ThreadPoolExecutor{
    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private final Logger log = Logger.getLogger("TimingThreadPool");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected void beforeExecute(Thread t,Runnable r){
        super.beforeExecute(t,r);
        log.fine(String.format("Thread $s : start %s",t,r));
        startTime.set(System.nanoTime());
    }

    protected void afterExecute(Runnable r,Throwable t){
        try{
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.fine(String.format("Thread %s : end %s , time = %dns",t,r,taskTime));

        }finally {
            super.afterExecute(r,t);
        }
    }

    protected void terminated(){
        try{
            log.info(String.format("Terninated: avg time = %ds",totalTime.get()/numTasks.get()));
        }finally {
            super.terminated();
        }
    }
}
