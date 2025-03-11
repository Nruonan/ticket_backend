package org.xzn.index12306.framework.starter.common.threadpool.support.eager;

import java.util.concurrent.LinkedBlockingQueue;
import lombok.Setter;

/**
 * @author Nruonan
 * @description 快速消费任务队列
 */
public class TaskQueue<R extends Runnable> extends LinkedBlockingQueue<Runnable> {

    @Setter
    private EagerThreadPoolExecutor executor;

    public TaskQueue(int capacity){
        super(capacity);
    }

    @Override
    public boolean offer(Runnable runnable) {
        int poolSize = executor.getPoolSize();
        // 如果有核心线程正在空闲，将任务加入阻塞队列，由核心线程进行处理任务
        if (executor.getSubmittedTaskCount() < poolSize){
            return super.offer(runnable);
        }
        // 当前线程池线程数量小于最大线程数，返回 False，根据线程池源码，会创建非核心线程
        if (poolSize < executor.getMaximumPoolSize()){
            return false;
        }
        // 如果当前线程池数量大于最大线程数，任务加入阻塞队列
        return super.offer(runnable);
    }

    public boolean retryOffer(Runnable o, long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException {
        // 检查执行器是否已经关闭
        if (executor.isShutdown()){
            // 如果执行器已经关闭，则抛出异常
            throw new java.util.concurrent.RejectedExecutionException("Executor is shutdown!");
        }
        // 调用父类的offer方法，在指定时间内提交任务
        return super.offer(o, timeout, unit);
    }
}
