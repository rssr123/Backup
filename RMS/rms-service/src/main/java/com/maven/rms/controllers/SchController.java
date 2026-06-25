package com.maven.rms.controllers;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.JobChainingJobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.SchedulerCustReq;
import com.maven.rms.models.SchedulerSeq;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.ServerInetUtils;

@RestController
@RequestMapping("/api/scheduler")
public class SchController {
    private final Scheduler scheduler;

    @Autowired
    private ServerInetUtils adapter;

    @Autowired
    private SchService schedulerService;

    @Autowired
    private RMSProperties rmsProperties;

    @Autowired
    private Environment env;

    public SchController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    // Add this new endpoint for Quartz connection diagnostics
    @PostMapping("/quartz-connection-status")
    public ResponseEntity<Map<String, Object>> getQuartzConnectionStatus(HttpServletRequest request) {
        Map<String, Object> status = new HashMap<>();

        try {
            // Get request information
            String fullUrl = Optional.ofNullable(request.getRequestURL())
                    .map(StringBuffer::toString)
                    .filter(s -> !s.trim().isEmpty())
                    .orElse("URL not available");

            String hostWithPort = Optional.ofNullable(request)
                    .map(req -> req.getServerName() + ":" + req.getServerPort() + " - ip:" + adapter.getServerIP())
                    .orElse("Host not available");

            status.put("requestUrl", fullUrl);
            status.put("hostInfo", hostWithPort);
            status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss a")));

            // Read Spring DataSource properties (new approach)
            Properties springProps = loadSpringDataSourceProperties();

            // Note: Database connection now comes from Spring DataSource, not
            // quartz.properties
            status.put("springJdbcUrl", springProps.getProperty("spring.datasource.url", "Not found"));
            status.put("springDbUsername", springProps.getProperty("spring.datasource.username", "Not found"));
            status.put("springDriver", springProps.getProperty("spring.datasource.driverClassName", "Not found"));
            status.put("springHikariMaxPoolSize",
                    springProps.getProperty("spring.datasource.hikari.maximum-pool-size", "Not found"));

            // Quartz configuration (now programmatic via QuartzConfig.java)
            status.put("quartzJobStoreClass", "org.quartz.impl.jdbcjobstore.JobStoreTX (configured programmatically)");
            status.put("quartzDataSourceUsage", "Uses Spring Boot's auto-configured DataSource");
            status.put("quartzConfigurationSource", "QuartzConfig.java + application.properties");

            // Scheduler status
            try {
                status.put("schedulerStarted", scheduler.isStarted());
                status.put("schedulerInStandbyMode", scheduler.isInStandbyMode());
                status.put("schedulerShutdown", scheduler.isShutdown());
                status.put("schedulerName", scheduler.getSchedulerName());
                status.put("schedulerInstanceId", scheduler.getSchedulerInstanceId());
            } catch (SchedulerException se) {
                status.put("schedulerError", se.getMessage());
            }

            // Quartz enable status from RMS properties
            String enableFlag = rmsProperties.getQuartzEnableStatus();
            status.put("quartzEnabled", "true".equalsIgnoreCase(enableFlag));

            // Show Spring DataSource properties for debugging
            Map<String, String> allSpringProps = new HashMap<>();
            for (String key : springProps.stringPropertyNames()) {
                if (key.toLowerCase().contains("password")) {
                    allSpringProps.put(key, "***HIDDEN***");
                } else {
                    allSpringProps.put(key, springProps.getProperty(key));
                }
            }
            status.put("allSpringDataSourceProperties", allSpringProps);

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            status.put("error", "Exception occurred: " + e.getMessage());
            status.put("errorClass", e.getClass().getSimpleName());
            status.put("stackTrace", buildStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    // Helper method to load Spring DataSource properties
    private Properties loadSpringDataSourceProperties() {
        Properties props = new Properties();
        try {
            // Load Spring DataSource properties from environment
            props.setProperty("spring.datasource.url",
                    env.getProperty("spring.datasource.url", "Not configured"));
            props.setProperty("spring.datasource.username",
                    env.getProperty("spring.datasource.username", "Not configured"));
            props.setProperty("spring.datasource.driverClassName",
                    env.getProperty("spring.datasource.driverClassName", "Not configured"));
            props.setProperty("spring.datasource.hikari.maximum-pool-size",
                    env.getProperty("spring.datasource.hikari.maximum-pool-size", "Not configured"));
            props.setProperty("spring.datasource.hikari.minimum-idle",
                    env.getProperty("spring.datasource.hikari.minimum-idle", "Not configured"));
            props.setProperty("spring.datasource.hikari.connection-timeout",
                    env.getProperty("spring.datasource.hikari.connection-timeout", "Not configured"));

            // Add Quartz-specific Spring properties
            props.setProperty("spring.quartz.job-store-type",
                    env.getProperty("spring.quartz.job-store-type", "Not configured"));
            props.setProperty("spring.quartz.jdbc.initialize-schema",
                    env.getProperty("spring.quartz.jdbc.initialize-schema", "Not configured"));
            props.setProperty("quartz.enable.tasks",
                    env.getProperty("quartz.enable.tasks", "Not configured"));
            props.setProperty("using.spring.schedulerFactory",
                    env.getProperty("using.spring.schedulerFactory", "Not configured"));

        } catch (Exception e) {
            props.setProperty("error", "Failed to load Spring DataSource properties: " + e.getMessage());
        }
        return props;
    }

    // Helper method to build stack trace for debugging
    private String buildStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().contains("com.maven.rms") ||
                    element.getClassName().contains("quartz") ||
                    element.getClassName().contains("informix")) {
                sb.append("  at ").append(element.toString()).append("\n");
            }
        }
        if (e.getCause() != null) {
            sb.append("Caused by: ").append(buildStackTrace((Exception) e.getCause()));
        }
        return sb.toString();
    }

    @PostMapping("/jobs")
    public List<Map<String, Object>> getAllScheduledJobs() throws SchedulerException {
        List<Map<String, Object>> jobDetailsList = new ArrayList<>();

        // Get all job group names
        List<String> jobGroupNames = scheduler.getJobGroupNames();

        // Loop through each group and retrieve job details
        for (String groupName : jobGroupNames) {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));

            for (JobKey jobKey : jobKeys) {
                Map<String, Object> jobDetails = new HashMap<>();
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                jobDetails.put("jobName", jobDetail.getKey().getName());
                jobDetails.put("groupName", jobDetail.getKey().getGroup());
                jobDetails.put("description", jobDetail.getDescription());

                // Retrieve associated triggers and their next fire times
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                List<Map<String, Object>> triggerList = new ArrayList<>();

                for (Trigger trigger : triggers) {
                    Map<String, Object> triggerInfo = new HashMap<>();
                    triggerInfo.put("triggerName", trigger.getKey().getName());
                    Date nextFireTime = trigger.getNextFireTime();

                    if (nextFireTime != null) {
                        // Convert nextFireTime to Asia/Kuala_Lumpur time zone
                        String formattedNextFireTime = formatDateInTimeZone(nextFireTime, "Asia/Singapore");
                        triggerInfo.put("nextFireTime", formattedNextFireTime);
                    }

                    if (trigger instanceof CronTrigger) {
                        triggerInfo.put("cronExpression", ((CronTrigger) trigger).getCronExpression());
                    }
                    triggerList.add(triggerInfo);
                }

                jobDetails.put("triggers", triggerList);
                jobDetailsList.add(jobDetails);
            }
        }

        return jobDetailsList;
    }

    private String formatDateInTimeZone(Date date, String timeZoneId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return dateFormat.format(date);
    }

    @PostMapping("/running-jobs")
    public ResponseEntity<List<Map<String, Object>>> getCurrentlyRunningJobs() throws SchedulerException {
        List<Map<String, Object>> runningJobs = new ArrayList<>();
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

        for (JobExecutionContext context : executingJobs) {
            Map<String, Object> jobInfo = new HashMap<>();
            JobDetail jobDetail = context.getJobDetail();

            jobInfo.put("jobName", jobDetail.getKey().getName());
            jobInfo.put("groupName", jobDetail.getKey().getGroup());
            jobInfo.put("description", jobDetail.getDescription());
            jobInfo.put("fireTime", context.getFireTime());
            jobInfo.put("nextFireTime", context.getNextFireTime());
            jobInfo.put("scheduledFireTime", context.getScheduledFireTime());
            jobInfo.put("isRecovering", context.isRecovering());

            runningJobs.add(jobInfo);
        }

        return ResponseEntity.ok(runningJobs);
    }

    @PostMapping("/triggerJob")
    public ResponseEntity<String> triggerJob(@RequestBody SchedulerCustReq schedulerCustReq,
            HttpServletRequest request)
            throws ClassNotFoundException {
        try {

            String jobName = schedulerCustReq.getI_function_nm();
            Class<? extends Job> jobClass = (Class<? extends Job>) Class
                    .forName("com.maven.rms.scheduler.jobs." + jobName);
            JobKey jobKey = JobKey.jobKey(jobName, "Group -1");
            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobKey).storeDurably().build();

            // Register this job to the scheduler
            scheduler.addJob(job, true);

            // scheduler.scheduleJob(job, quartzConfig.insUserNewJobTriggerMan());
            scheduler.triggerJob(jobKey);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
            String formattedTime = LocalDateTime.now().format(formatter);

            String fullUrl = Optional.ofNullable(request.getRequestURL())
                    .map(StringBuffer::toString)
                    .filter(s -> !s.trim().isEmpty())
                    .orElse("URL not available");

            String hostWithPort = Optional.ofNullable(request)
                    .map(req -> req.getServerName() + ":" + req.getServerPort() + " - ip:" + adapter.getServerIP())
                    .orElse("Host not available");

            String enableFlag = rmsProperties.getQuartzEnableStatus();

            String statusMessage = "true".equalsIgnoreCase(enableFlag) ? "Scheduler is ENABLED"
                    : "Scheduler is DISABLED";

            return ResponseEntity.ok(jobKey + " is triggered successfully at " + formattedTime
                    + " via " + hostWithPort + ". " + statusMessage + ".");

        } catch (SchedulerException e) {
            // Enhanced error response with Quartz connection details
            String errorDetails = buildQuartzErrorDetails(e, request);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error triggering the job: " + e.getMessage() + "\n" + errorDetails);
        }
    }

    // Helper method to build detailed Quartz error information
    private String buildQuartzErrorDetails(Exception e, HttpServletRequest request) {
        StringBuilder details = new StringBuilder();

        try {
            // Request information
            String fullUrl = Optional.ofNullable(request.getRequestURL())
                    .map(StringBuffer::toString)
                    .filter(s -> !s.trim().isEmpty())
                    .orElse("URL not available");

            String hostWithPort = Optional.ofNullable(request)
                    .map(req -> req.getServerName() + ":" + req.getServerPort() + " - ip:" + adapter.getServerIP())
                    .orElse("Host not available");

            // Load Spring DataSource properties (new approach)
            Properties springProps = loadSpringDataSourceProperties();

            details.append("\n--- Quartz Connection Diagnostics ---");
            details.append("\nTimestamp: ")
                    .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss a")));
            details.append("\nRequest URL: ").append(fullUrl);
            details.append("\nHost Info: ").append(hostWithPort);
            details.append("\nSpring DB URL: ")
                    .append(springProps.getProperty("spring.datasource.url", "Not configured"));
            details.append("\nSpring DB User: ")
                    .append(springProps.getProperty("spring.datasource.username", "Not configured"));
            details.append("\nSpring Driver: ")
                    .append(springProps.getProperty("spring.datasource.driverClassName", "Not configured"));
            details.append("\nSpring Max Pool Size: ")
                    .append(springProps.getProperty("spring.datasource.hikari.maximum-pool-size", "Not configured"));
            details.append("\nQuartz Job Store Type: ")
                    .append(springProps.getProperty("spring.quartz.job-store-type", "Not configured"));
            details.append("\nQuartz Configuration: QuartzConfig.java (uses Spring DataSource)");
            details.append("\nError Type: ").append(e.getClass().getSimpleName());

            // Scheduler status
            try {
                details.append("\nScheduler Started: ").append(scheduler.isStarted());
                details.append("\nScheduler Standby: ").append(scheduler.isInStandbyMode());
                details.append("\nScheduler Shutdown: ").append(scheduler.isShutdown());
                details.append("\nScheduler Name: ").append(scheduler.getSchedulerName());
                details.append("\nScheduler Instance ID: ").append(scheduler.getSchedulerInstanceId());
            } catch (SchedulerException se) {
                details.append("\nScheduler Status Error: ").append(se.getMessage());
            }

            // Quartz enable status
            String enableFlag = rmsProperties.getQuartzEnableStatus();
            details.append("\nQuartz Enabled: ").append("true".equalsIgnoreCase(enableFlag));

            // Check if this is a connection error and add more details
            if (e.getMessage() != null && (e.getMessage().contains("JDBC") || e.getMessage().contains("connection")
                    || e.getMessage().contains("database"))) {
                details.append("\n--- DATABASE CONNECTION ERROR DETECTED ---");
                details.append("\nThis appears to be a database connection issue.");
                details.append("\nPlease check:");
                details.append("\n1. Database server is running and accessible");
                details.append("\n2. Database credentials are correct");
                details.append("\n3. Network connectivity to database server");
                details.append("\n4. Informix server name and port are correct");
            }

        } catch (Exception diagEx) {
            details.append("\nDiagnostics Error: ").append(diagEx.getMessage());
        }

        return details.toString();
    }

    // Stop the scheduler
    @PostMapping("/stop")
    public ResponseEntity<String> stopScheduler() {
        try {
            scheduler.standby();
            return ResponseEntity.ok("Scheduler stopped successfully.");
        } catch (SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to stop scheduler: " + e.getMessage());
        }
    }

    // Restart the scheduler
    @PostMapping("/restart")
    public ResponseEntity<String> restartScheduler() {
        try {
            scheduler.start();
            return ResponseEntity.ok("Scheduler restarted successfully.");
        } catch (SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to stop scheduler: " + e.getMessage());
        }
    }

    @PostMapping("/chaingroup")
    public ResponseEntity<?> manualTriggerChainGroup(@RequestBody SchedulerCustReq schedulerCustReq) {

        try {
            List<SchedulerSeq> result = schedulerService.sp_getchaingroup(schedulerCustReq);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Invalid chain name. Please enter a valid ones");
            }

            else {

                if (result.get(result.size() - 1).getScheduler_status().equals("P")) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("This job chain is executing now. Please wait for it to complete.");
                }

                else {
                    // Get the chain group of the first job
                    Integer chainGroup = result.get(0).getChain_group();
                    // Update the status of the chain group to 'P' (Pending)
                    SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
                    schedulerUpdRequest.setI_chain_group(chainGroup);
                    ;
                    schedulerUpdRequest.setI_sch_status("P");
                    // Call the schedulerService to update the status
                    schedulerService.sp_updschstatus(schedulerUpdRequest);

                    Set<JobKey> defaultJobGroup = scheduler
                            .getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP));

                    // if (!defaultJobGroup.isEmpty()) {
                    // // Delete all jobs in the DEFAULT group
                    // scheduler.deleteJobs(new ArrayList<>(defaultJobGroup));
                    // }

                    JobChainingJobListener jobListener = new JobChainingJobListener("customChainListener");
                    String firstJobName = result.get(0).getFunction_nm();
                    Class<? extends Job> jobClass = (Class<? extends Job>) Class
                            .forName("com.maven.rms.scheduler.jobs." + firstJobName);

                    JobDetail firstJobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity(firstJobName)
                            .withDescription(firstJobName)
                            .storeDurably()
                            .build();

                    // Trigger firstTrigger = TriggerBuilder.newTrigger()
                    // .withIdentity(firstJobName + "Trigger")
                    // //
                    // .withSchedule(CronScheduleBuilder.cronSchedule(result.get(0).getFrequency()).inTimeZone(TimeZone.getTimeZone("Asia/Singapore")))
                    // .build();

                    // scheduler.scheduleJob(firstJobDetail, firstTrigger);

                    for (int i = 0; i < result.size(); i++) {
                        String jobName = result.get(i).getFunction_nm();
                        Class<? extends Job> jobClass1 = (Class<? extends Job>) Class
                                .forName("com.maven.rms.scheduler.jobs." + jobName);
                        JobDetail jobDetail = JobBuilder.newJob(jobClass1)
                                .withIdentity(jobName)
                                .withDescription(jobName)
                                .storeDurably()
                                .build();
                        scheduler.addJob(jobDetail, true);
                    }
                    if (result.size() < 2) {
                        throw new IllegalArgumentException(
                                "The scheduler list must contain at least two jobs to create a job chain.");
                    }

                    List<JobKey> jobKeys = new ArrayList<>();

                    for (int i = 0; i < result.size() - 1; i++) {
                        SchedulerSeq currentJob = result.get(i);
                        SchedulerSeq nextJob = result.get(i + 1);
                        System.out.println(currentJob.getJob_name() + " -> " + nextJob.getJob_name());

                        JobKey currentJobKey = new JobKey(currentJob.getJob_name());
                        JobKey nextJobKey = new JobKey(nextJob.getJob_name());

                        jobKeys.add(currentJobKey);
                        jobKeys.add(nextJobKey);

                        // Add the job chain link
                        jobListener.addJobChainLink(currentJobKey, nextJobKey);
                    }

                    scheduler.getListenerManager().addJobListener(jobListener);

                    scheduler.triggerJob(firstJobDetail.getKey());

                    return ResponseEntity.ok("Custom Job Chain Executed Successfully.");

                }

            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing stored procedure: " + e.getMessage());
        }
    }

    // private boolean isGroupScheduledWithinNext5Minutes(String groupName) throws
    // SchedulerException {
    // // Retrieve all job keys in the specified group
    // Set<JobKey> jobKeys =
    // scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));

    // // Get the current time
    // Date now = new Date();

    // for (JobKey jobKey : jobKeys) {
    // // Retrieve all triggers for this job
    // List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

    // for (Trigger trigger : triggers) {
    // Date previousFireTime = trigger.getPreviousFireTime();
    // Date nextFireTime = trigger.getNextFireTime();

    // // Check if previous fire time is within the last 5 minutes
    // if (previousFireTime != null) {
    // Date fiveMinutesAfterPrevious = new Date(previousFireTime.getTime() + (5 * 60
    // * 1000));
    // if (!now.after(fiveMinutesAfterPrevious)) {
    // System.out.println("Job " + jobKey.getName() + " in group '" + groupName +
    // "' is within the blocking window. Previous Fire: " + previousFireTime);
    // return true; // Block execution if within the blocking window
    // }
    // }

    // // Check if next fire time is within the next 5 minutes
    // if (nextFireTime != null) {
    // Date fiveMinutesBeforeNext = new Date(nextFireTime.getTime() - (5 * 60 *
    // 1000));
    // if (!now.before(fiveMinutesBeforeNext)) {
    // System.out.println("Job " + jobKey.getName() + " in group '" + groupName +
    // "' is within the blocking window. Next Fire: " + nextFireTime);
    // return true; // Block execution if within the blocking window
    // }
    // }
    // }
    // }
    // return false; // No jobs in the group are scheduled within the blocking
    // window
    // }

    // @PostMapping("/chaincustom")
    // public ResponseEntity<?> getJobsBetween(@RequestBody SchedulerCustReq
    // schedulerCustReq) {

    // try {
    // List<SchedulerSeq> result =
    // schedulerService.sp_getschcustom(schedulerCustReq);

    // if (result.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Jobs
    // must be in the same group and must be in start job's sequence must come
    // before end job'sequence.");
    // }

    // else{
    // // Get the chain group of the first job
    // String chainGroup = "Group " + result.get(0).getChain_group();

    // // Check if the next fire time for any job in this group is within the next 5
    // minutes
    // if (isGroupScheduledWithinNext5Minutes(chainGroup)) {
    // return ResponseEntity.status(HttpStatus.CONFLICT)
    // .body("The group is scheduled to run within the next 5 minutes. Cannot
    // trigger the custom chain.");
    // }

    // Set<JobKey> defaultJobGroup =
    // scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP));

    // if (!defaultJobGroup.isEmpty()) {
    // // Delete all jobs in the DEFAULT group
    // scheduler.deleteJobs(new ArrayList<>(defaultJobGroup));
    // }

    // JobChainingJobListener jobListener = new
    // JobChainingJobListener("customChainListener");
    // String firstJobName = result.get(0).getFunction_nm();
    // Class<? extends Job> jobClass = (Class<? extends Job>)
    // Class.forName("com.maven.rms.scheduler.jobs." + firstJobName);

    // JobDetail firstJobDetail = JobBuilder.newJob(jobClass)
    // .withIdentity(firstJobName)
    // .withDescription(firstJobName)
    // .storeDurably()
    // .build();

    // Trigger firstTrigger = TriggerBuilder.newTrigger()
    // .withIdentity(firstJobName + "Trigger")
    // .withSchedule(CronScheduleBuilder.cronSchedule(result.get(0).getFrequency()).inTimeZone(TimeZone.getTimeZone("Asia/Singapore")))
    // .build();

    // scheduler.scheduleJob(firstJobDetail, firstTrigger);

    // for (int i = 1; i < result.size(); i++) {
    // String jobName = result.get(i).getFunction_nm();
    // Class<? extends Job> jobClass1 = (Class<? extends Job>) Class
    // .forName("com.maven.rms.scheduler.jobs." + jobName);
    // JobDetail jobDetail = JobBuilder.newJob(jobClass1)
    // .withIdentity(jobName)
    // .withDescription(jobName)
    // .storeDurably()
    // .build();
    // scheduler.addJob(jobDetail, true);
    // }
    // if (result.size() < 2) {
    // throw new IllegalArgumentException(
    // "The scheduler list must contain at least two jobs to create a job chain.");
    // }

    // List<JobKey> jobKeys = new ArrayList<>();

    // for (int i = 0; i < result.size() - 1; i++) {
    // SchedulerSeq currentJob = result.get(i);
    // SchedulerSeq nextJob = result.get(i + 1);
    // System.out.println(currentJob.getJob_name() + " -> " +
    // nextJob.getJob_name());

    // JobKey currentJobKey = new JobKey(currentJob.getJob_name());
    // JobKey nextJobKey = new JobKey(nextJob.getJob_name());

    // jobKeys.add(currentJobKey);
    // jobKeys.add(nextJobKey);

    // // Add the job chain link
    // jobListener.addJobChainLink(currentJobKey, nextJobKey);
    // }

    // scheduler.getListenerManager().addJobListener(jobListener);

    // scheduler.triggerJob(firstJobDetail.getKey());

    // return ResponseEntity.ok("Custom Job Chain Executed Successfully.");
    // }

    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("Error executing stored procedure: " + e.getMessage());
    // }
    // }
}