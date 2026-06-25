package com.maven.rms.scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobChainingJobListener;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.maven.rms.config.AutoWiringSpringBeanJobFactory;
import com.maven.rms.models.SchedulerSeq;
import com.maven.rms.services.SchService;

import lombok.extern.slf4j.Slf4j;

// Scheduler Configuration (sequential & dynamic) created by Wei Ern
@Configuration
@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
@Slf4j
public class QuartzConfig {
    @Autowired
    private SchService schedulerService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${quartz.enable.tasks}")
    private boolean enableQuartz;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    public Properties quartzProperties() throws IOException {
        Properties properties = new Properties();

        // Scheduler configuration
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.instanceName", "MyClusteredScheduler");

        // Thread pool configuration
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        // Job store configuration with clustering enabled
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass",
                "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.dataSource", "quartzDataSource");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "q");

        // Enable clustering
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");

        // CRITICAL: Informix-specific fixes for physical-order read issues
        // These properties are ESSENTIAL for 12-machine clustering with Informix
        properties.setProperty("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        properties.setProperty("org.quartz.jobStore.acquireTriggersWithinLock", "true");
        properties.setProperty("org.quartz.jobStore.lockHandler.class",
                "org.quartz.impl.jdbcjobstore.StdRowLockSemaphore");

        // INFORMIX-SPECIFIC: Use row-level locking to prevent physical-order conflicts
        properties.setProperty("org.quartz.jobStore.selectWithLockSQL",
                "SELECT * FROM {0}LOCKS WHERE SCHED_NAME = {1} AND LOCK_NAME = ? FOR UPDATE");

        // NEW: Additional Informix optimizations for large clusters
        properties.setProperty("org.quartz.jobStore.txIsolationLevelSerializable", "false");
        //properties.setProperty("org.quartz.jobStore.txIsolationLevelReadCommitted", "true");	//Causes deployment error! 20250910 - Brian

        // Reduce contention in large clusters (12 machines)
        //properties.setProperty("org.quartz.jobStore.idleWaitTime", "30000");
        properties.setProperty("org.quartz.jobStore.dbRetryInterval", "15000");

        // Informix-specific: Prevent deadlocks in clustering
        properties.setProperty("org.quartz.jobStore.dontSetAutoCommitFalse", "false");
        //properties.setProperty("org.quartz.jobStore.dontSetNonManagedTXConnectionAutoCommitFalse", "false");	//Causes deployment error! 20250910 - Brian

        // Quartz DataSource configuration - exactly like your working quartz.properties
        properties.setProperty("org.quartz.dataSource.quartzDataSource.driver", "com.informix.jdbc.IfxDriver");

        // Use the original URL without modification - Informix doesn't use standard URL
        // parameters
        properties.setProperty("org.quartz.dataSource.quartzDataSource.URL", datasourceUrl);
        properties.setProperty("org.quartz.dataSource.quartzDataSource.user", datasourceUsername);
        properties.setProperty("org.quartz.dataSource.quartzDataSource.password", datasourcePassword);

        // CRITICAL: Connection pool settings for 12-machine cluster
        properties.setProperty("org.quartz.dataSource.quartzDataSource.maxPoolSize", "5");
        properties.setProperty("org.quartz.dataSource.quartzDataSource.minPoolSize", "2");
        properties.setProperty("org.quartz.dataSource.quartzDataSource.maxIdleTime", "300");
        properties.setProperty("org.quartz.dataSource.quartzDataSource.idleConnectionValidationSeconds", "50");
        properties.setProperty("org.quartz.dataSource.quartzDataSource.validateOnCheckout", "true");

        // Additional connection pool optimizations for Informix clustering
        //properties.setProperty("org.quartz.dataSource.quartzDataSource.discardIdleConnectionsSeconds", "300");	//Causes deployment error! 20250910 - Brian

        return properties;
    }

    @Bean
    public JobFactory jobFactory() {
        // AutowiringSpringBeanJobFactorySch jobFactory = new
        // AutowiringSpringBeanJobFactorySch();
        // jobFactory.setApplicationContext(applicationContext);
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // Use Spring's JobFactory for proper dependency injection
        factory.setJobFactory(jobFactory());
        // Set Quartz properties - Quartz will manage its own datasource
        factory.setQuartzProperties(quartzProperties());
        // DON'T set Spring's DataSource - let Quartz manage its own connection pool
        // factory.setDataSource(dataSource);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setAutoStartup(enableQuartz);
        return factory;
    }
    /*
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        // AutoWiringSpringBeanJobFactory jobFactory = new
        // AutoWiringSpringBeanJobFactory();

        // jobFactory.setApplicationContext(applicationContext);
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        return jobFactory;
    }
	*/
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException, ClassNotFoundException {

        Scheduler scheduler = factory.getScheduler();
        JobChainingJobListener jobListener = new JobChainingJobListener("ChainListener");
        GroupJobListener groupJobListener = new GroupJobListener(schedulerService);
        scheduler.getListenerManager().addJobListener(groupJobListener);

        if (enableQuartz) {
            List<SchedulerSeq> schedulerSeqList = schedulerService.sp_getschseq();

            List<SchedulerSeq> schedulerSeqListInd = schedulerService.sp_getschind();

            // scheduler.clear();
            // #region Individually Scheduled Jobs
            // Loop through the list and create job details, triggers, and schedule them
            for (SchedulerSeq seq : schedulerSeqListInd) {
                String jobName = seq.getFunction_nm();
                String newCronExpression = seq.getFrequency();

                // Create a JobKey for this job
                JobKey jobKey = new JobKey(jobName, "Individual Group");

                if (scheduler.checkExists(jobKey)) {
                    // System.out.println("Job '" + jobName + "' already exists. Checking cron
                    // expression...");

                    // Check if the cron expression needs to be updated
                    List<? extends Trigger> existingTriggers = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : existingTriggers) {
                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            String currentCronExpression = cronTrigger.getCronExpression();

                            // If the cron expression is different, reschedule the job
                            if (!currentCronExpression.equals(newCronExpression)) {
                                // System.out.println("Cron expression for job '" + jobName + "' has changed.
                                // Rescheduling...");

                                // Create a new trigger with the updated cron expression
                                Trigger newTrigger = TriggerBuilder.newTrigger()
                                        .withIdentity(jobName + "_Trigger")
                                        .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression)
                                                .inTimeZone(TimeZone.getTimeZone("Asia/Singapore"))
                                                .withMisfireHandlingInstructionFireAndProceed())
                                        .forJob(jobKey)
                                        .build();

                                // Reschedule the job with the new trigger
                                scheduler.rescheduleJob(cronTrigger.getKey(), newTrigger);
                            } else {
                                System.out.println("Cron expression for job '" + jobName + "' is unchanged.");
                            }
                        }
                    }
                } else {
                    System.out.println("Adding new job: " + jobName);
                    // Create the JobDetail object
                    Class<? extends Job> jobClass = (Class<? extends Job>) Class
                            .forName("com.maven.rms.scheduler.jobs." + jobName);
                    JobDetail jobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity(jobKey)
                            .withDescription(jobName)
                            .requestRecovery(true)
                            .storeDurably()
                            .build();

                    // Create a CronTrigger for this job
                    Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(jobName + "_Trigger")
                            .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression)
                                    .inTimeZone(TimeZone.getTimeZone("Asia/Singapore"))
                                    .withMisfireHandlingInstructionFireAndProceed())
                            .forJob(jobDetail)
                            .build();

                    // Schedule the new job
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
            // #endregion

            // get how many groups are there in the job chain
            Integer count_group = schedulerSeqList.get(0).getCount_group();

            // #region Job Chain
            // Create a list of job chains based on the max count_group value
            List<List<SchedulerSeq>> jobGroups = new ArrayList<>(count_group);

            int previousChainGroup = -1; // Initialize once, outside the loop

            for (SchedulerSeq seq : schedulerSeqList) {
                int currentChainGroup = seq.getChain_group();
                // If the chain group changes, add to a new group
                if (currentChainGroup != previousChainGroup) {
                    jobGroups.add(new ArrayList<>()); // Add a new list for the new group
                }
                // Add the job to the latest group
                jobGroups.get(jobGroups.size() - 1).add(seq);
                // Update the previousChainGroup to the current one
                previousChainGroup = currentChainGroup;
            }

            for (int i = 0; i < jobGroups.size(); i++) {
                for (int j = 0; j < jobGroups.get(i).size(); j++) {
                    String jobName = jobGroups.get(i).get(j).getFunction_nm();
                    String groupName = "Group " + jobGroups.get(i).get(j).getChain_group();
                    JobKey jobKey = new JobKey(jobName, groupName);

                    Class<? extends Job> jobClass = (Class<? extends Job>) Class
                            .forName("com.maven.rms.scheduler.jobs." + jobName);

                    // Create a new JobDetail object
                    JobDetail jobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity(jobKey)
                            .withDescription(jobName)
                            .requestRecovery(true)
                            .storeDurably() // Store without scheduling by default
                            .build();

                    // Check if the job already exists
                    if (scheduler.checkExists(jobKey)) {
                        // System.out.println("Job '" + jobName + "' already exists.");

                        // Retrieve and compare the cron expression of the existing trigger
                        List<? extends Trigger> existingTriggers = scheduler.getTriggersOfJob(jobKey);
                        for (Trigger trigger : existingTriggers) {
                            if (trigger instanceof CronTrigger) {
                                CronTrigger cronTrigger = (CronTrigger) trigger;
                                String currentCronExpression = cronTrigger.getCronExpression();
                                String newCronExpression = jobGroups.get(i).get(j).getFrequency();

                                if (!currentCronExpression.equals(newCronExpression) && j == 0) {
                                    // System.out.println("Cron expression for '" + jobName + "' has changed.
                                    // Rescheduling...");

                                    // Create a new trigger with the updated cron expression
                                    Trigger newTrigger = TriggerBuilder.newTrigger()
                                            .withIdentity(jobName + "Trigger", groupName)
                                            .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression)
                                                    .inTimeZone(TimeZone.getTimeZone("Asia/Singapore"))
                                                    .withMisfireHandlingInstructionFireAndProceed())
                                            .forJob(jobDetail)
                                            .build();

                                    // Reschedule only the first job in the group
                                    scheduler.rescheduleJob(cronTrigger.getKey(), newTrigger);
                                } else {
                                    // System.out.println("Cron expression for '" + jobName + "' is the same. No
                                    // rescheduling needed.");
                                }
                            }
                        }
                    } else {
                        // System.out.println("Adding new job: " + jobName);
                        scheduler.addJob(jobDetail, false); // Add job without scheduling

                        // Schedule only the first job in the group
                        if (j == 0) {
                            // System.out.println("Scheduling first job in group: " + jobName);

                            Trigger trigger = TriggerBuilder.newTrigger()
                                    .withIdentity(jobName + "Trigger", groupName)
                                    .withSchedule(
                                            CronScheduleBuilder.cronSchedule(jobGroups.get(i).get(j).getFrequency())
                                                    .inTimeZone(TimeZone.getTimeZone("Asia/Singapore"))
                                                    .withMisfireHandlingInstructionFireAndProceed())
                                    .forJob(jobDetail)
                                    .build();

                            scheduler.scheduleJob(trigger); // Schedule the first job with a trigger
                        }
                    }
                }
                // Ensure each group has at least two jobs for a valid chain
                if (jobGroups.get(i).size() < 2) {
                    throw new IllegalArgumentException(
                            "Each job group must contain at least two jobs to create a valid chain.");
                }
            }

            // Adding Job Chains
            for (int i = 0; i < jobGroups.size(); i++) {
                for (int j = 0; j < jobGroups.get(i).size() - 1; j++) {
                    SchedulerSeq currentJob = jobGroups.get(i).get(j);
                    SchedulerSeq nextJob = jobGroups.get(i).get(j + 1);
                    // System.out.println("Current Job: " + currentJob.getChain_group() +
                    // currentJob.getFunction_nm() + " Next Job: " + nextJob.getChain_group() +
                    // nextJob.getFunction_nm());

                    JobKey currentJobKey = new JobKey(currentJob.getFunction_nm(),
                            "Group " + currentJob.getChain_group());
                    JobKey nextJobKey = new JobKey(nextJob.getFunction_nm(), "Group " + nextJob.getChain_group());
                    // Add the job chain link
                    jobListener.addJobChainLink(currentJobKey, nextJobKey);
                }
            }

            // #endregion
            scheduler.getListenerManager().addJobListener(jobListener);

            if (enableQuartz) {
                scheduler.start();
            }
        }
        return scheduler;
    }
}
