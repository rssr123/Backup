package com.maven.rms.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.AdviceMode;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync(mode = AdviceMode.ASPECTJ)
@Slf4j
public class AsyncPaymentClearanceConfig {

    // Thread pool configuration constants
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final int QUEUE_CAPACITY = 1000;
    private static final String THREAD_NAME_PREFIX = "paymentClearanceThread-";
    private static final int AWAIT_TERMINATION_SECONDS = 30;
    private static final int KEEP_ALIVE_SECONDS = 60;

    // Additional configuration for thread lifecycle
    private static final boolean ALLOW_CORE_THREAD_TIMEOUT = true;
    private static final boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = true;

    @Bean(name = "paymentClearanceExecutor")
    public Executor taskExecutor() {
        log.info(
                "Initializing payment clearance executor with configuration - CorePoolSize: {}, MaxPoolSize: {}, QueueCapacity: {}",
                CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY);

        log.info(
                "Thread pool behavior: Tasks 1-{} execute immediately, Tasks {}-{} queue, Tasks {}-{} create new threads, Tasks {}+ run in caller thread (BLOCKING)",
                CORE_POOL_SIZE,
                CORE_POOL_SIZE + 1, CORE_POOL_SIZE + QUEUE_CAPACITY,
                CORE_POOL_SIZE + QUEUE_CAPACITY + 1, CORE_POOL_SIZE + QUEUE_CAPACITY + (MAX_POOL_SIZE - CORE_POOL_SIZE),
                CORE_POOL_SIZE + QUEUE_CAPACITY + (MAX_POOL_SIZE - CORE_POOL_SIZE) + 1);

        return createSpringThreadPool();
    }

    private Executor createSpringThreadPool() {
        try {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

            // Set thread pool configuration
            executor.setCorePoolSize(CORE_POOL_SIZE);
            executor.setMaxPoolSize(MAX_POOL_SIZE);
            executor.setQueueCapacity(QUEUE_CAPACITY);
            executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

            // Enhanced configuration for better error handling
            executor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);
            executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
            executor.setAllowCoreThreadTimeOut(ALLOW_CORE_THREAD_TIMEOUT);
            executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);

            // Custom rejection policy for better error handling
            executor.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());

            // Initialize the executor
            executor.initialize();

            log.info(
                    "Successfully initialized Spring Java thread pool - CorePoolSize: {}, MaxPoolSize: {}, QueueCapacity: {}, ThreadNamePrefix: '{}'",
                    CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY, THREAD_NAME_PREFIX);

            return executor;

        } catch (IllegalArgumentException ex) {
            log.error(
                    "Invalid thread pool configuration parameters - CorePoolSize: {}, MaxPoolSize: {}, QueueCapacity: {}",
                    CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY, ex);
            throw new RuntimeException("Invalid thread pool configuration: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Failed to initialize Spring Java thread pool", ex);
            throw new RuntimeException("Spring thread pool initialization failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Custom rejection handler to provide better error logging and handling
     */
    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("Task rejected by payment clearance executor. " +
                    "ActiveCount: {}, CorePoolSize: {}, MaximumPoolSize: {}, QueueSize: {}, TaskCount: {}. " +
                    "Running task in caller thread to avoid loss.",
                    executor.getActiveCount(),
                    executor.getCorePoolSize(),
                    executor.getMaximumPoolSize(),
                    executor.getQueue().size(),
                    executor.getTaskCount());

            // Strategy: Run in caller thread (this will WAIT/BLOCK until task completes)
            log.info("Running rejected payment clearance task in caller thread");
            r.run();

            // Alternative strategies (commented out):
            // 1. Throw exception (original behavior - no waiting)
            // throw new RuntimeException("Payment clearance task rejected due to thread
            // pool saturation");

            // 2. Discard silently (not recommended for payment processing)
            // log.warn("Discarding rejected payment clearance task");
        }
    }
}