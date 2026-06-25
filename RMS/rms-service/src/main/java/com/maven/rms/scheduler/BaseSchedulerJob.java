package com.maven.rms.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.maven.rms.logging.LoggingContextUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for all Quartz jobs that ensures proper logging context
 * Every scheduler job should extend this to have correlation IDs in logs
 */
@Slf4j
public abstract class BaseSchedulerJob implements Job {

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();
        String taskIdentifier = jobGroup + "." + jobName;

        // Set up logging context for this background task
        LoggingContextUtil.setupBackgroundTaskContext(taskIdentifier);

        try {
            log.info("Starting scheduler job: {}", taskIdentifier);

            // Call the actual job implementation
            executeJob(context);

            log.info("Completed scheduler job: {}", taskIdentifier);

        } catch (Exception e) {
            log.error("Error in scheduler job: " + taskIdentifier, e);
            throw new JobExecutionException(e);
        } finally {
            // Clear logging context
            LoggingContextUtil.clearContext();
        }
    }

    /**
     * Implement this method with your actual job logic
     */
    protected abstract void executeJob(JobExecutionContext context) throws Exception;
}
