package org.xzn.index12306.framework.starter.common.threadpool.support.eager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nruonan
 * @description 快速消费线程池
 */
public class EagerThreadPoolExecutor extends ThreadPoolExecutor {

    public EagerThreadPoolExecutor(int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedTaskCount.decrementAndGet();
    }

    @Override
    /**
     * 执行给定的命令
     * 
     * @param command 可运行的命令，不应为null
     * 
     * 此方法首先会增加提交任务计数，然后尝试执行给定的命令
     * 如果命令被拒绝，会尝试重新提交到任务队列中
     * 如果重试失败或者在过程中被中断，会相应地减少提交任务计数，并抛出异常
     */
    public void execute(Runnable command) {
        // 增加提交的任务计数
        submittedTaskCount.incrementAndGet();
        
        try {
            // 尝试执行命令
            super.execute(command);
        } catch (RejectedExecutionException ex) {
            // 如果命令被拒绝，尝试重新提交到任务队列
            TaskQueue taskQueue = (TaskQueue) super.getQueue();
            try {
                if (!taskQueue.retryOffer(command, 0, TimeUnit.MILLISECONDS)) {
                    // 如果重试失败，减少提交的任务计数，并抛出异常
                    submittedTaskCount.decrementAndGet();
                    throw new RejectedExecutionException("Queue capacity is full.", ex);
                }
            } catch (InterruptedException iex) {
                // 如果在重试过程中被中断，减少提交的任务计数，并抛出异常
                submittedTaskCount.decrementAndGet();
                throw new RejectedExecutionException(iex);
            }
        } catch (Exception ex) {
            // 如果遇到其他异常，减少提交的任务计数，并抛出异常
            submittedTaskCount.decrementAndGet();
            throw ex;
        }
    }
}
