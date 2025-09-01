package com.crrc.sys.common;

import java.util.concurrent.*;

/**
 * 自定义单例线程池
 * 注意：线程池使用完毕后记得shutdown()
 *
 * @author 谭永强
 * @date 2024-05-24
 */
public class MyThreadPoolExecutor {

    /**
     * 创建线程池
     *
     * @param maximumPoolSize 线程池中能够容纳同时执行的最大线程数，此值必须大于等于1
     * @return 线程池
     */
    public static ThreadPoolExecutor getThreadPoolExecutor(int maximumPoolSize) {
        //1.线程池中的常驻核心线程数
        final int corePoolSize = 1;
        //2.线程池中能够容纳同时执行的最大线程数，此值必须大于等于1
        if (maximumPoolSize <= 0) {
            //CPU核心数
            final int cpuCoreNum = Runtime.getRuntime().availableProcessors();
            maximumPoolSize = (cpuCoreNum - 1);
        }
        //3.多余的空闲线程的存活时间。当前池中线程数量超过corePoolSize时，当空闲时间达到keepAliveTime的线程会被销毁，直到剩余线程数量等于 corePoolSize
        final int keepLiveTime = 0;
        //4.keepAliveTime的时间单位
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        //5.任务队列，被提交但尚未被执行的任务
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(maximumPoolSize * 10);
        //6.表示生成线程池中工作线程的工厂， 用于创建线程，一般默认的即可
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //7.策略处理器。当任务队列已满，工作线程也达到了maximumPoolSize，新增的工作任务将按照某个既定的策略执行。
        //AbortPolicy:默认策略，直接跑出异常阻止系统正常运行
        //CallerRunsPolicy:“调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将任务回馈至发起方比如main线程
        //DiscardOldestPolicy:抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务
        //DiscardPolicy:直接丢弃任务，不给予任何处理也不跑出异常，如果允许任务丢失，这是最好的一种方案
        RejectedExecutionHandler policy = new ThreadPoolExecutor.CallerRunsPolicy();
        //自定义创建线程池对象
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepLiveTime, timeUnit, queue, threadFactory, policy);
    }
}
