package com.maven.rms.scheduler;

import java.util.Set;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.JobListenerSupport;

import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.services.SchService;

public class GroupJobListener extends JobListenerSupport {

    private final SchService schedulerService;

    public GroupJobListener(SchService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public String getName() {
        return "GroupJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        String groupName = jobKey.getGroup();

        if (groupName == Scheduler.DEFAULT_GROUP) {
            return;
        }

        // Check if the job is the first job in its group
        if (isFirstJobInGroup(context.getScheduler(), jobKey)) {
            System.out.println("First job of group '" + groupName + "' executed: " + jobKey);

            Integer groupNumber = extractGroupNumber(groupName);
            if (groupNumber == null) {
                System.out.println("Skipping scheduler status update for group '" + groupName
                        + "' - no valid group number found.");
                return;
            }

            SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
            schedulerUpdRequest.setI_chain_group(groupNumber);
            schedulerUpdRequest.setI_sch_status("P");

            // Call the schedulerService to update the status
            schedulerService.sp_updschstatus(schedulerUpdRequest);
        }
    }

    // Helper method to check if the job is the first in its group
    private boolean isFirstJobInGroup(Scheduler scheduler, JobKey jobKey) {
        try {
            // Retrieve all job keys in the group
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobKey.getGroup()));

            // Check if the current job is the first job in the group (based on natural
            // order)
            return jobKeys.stream().sorted().findFirst().map(key -> key.equals(jobKey)).orElse(false);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Integer extractGroupNumber(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return null;
        }

        String[] parts = groupName.split(" ");
        String lastPart = parts[parts.length - 1];

        try {
            return Integer.parseInt(lastPart);
        } catch (NumberFormatException e) {
            // Group name doesn't end with a number (e.g., just "Group" or "DEFAULT")
            System.out.println("Warning: Group name '" + groupName
                    + "' does not contain a valid number. Skipping group processing.");
            return null;
        }
    }

}
