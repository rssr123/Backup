package com.maven.rms.utils;

import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RMSLogger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

    // Log info in console
    public static void info(String message) {
        // System.out.println(ANSI_BLUE + message + ANSI_RESET);
        log.info(ANSI_BLUE + message + ANSI_RESET);
    }

    public static void error(String message) {
        // System.out.println(ANSI_RED + message + ANSI_RESET);
        log.error(ANSI_RED + message + ANSI_RESET);
    }

    public static void warning(String message) {
        // System.out.println(ANSI_YELLOW + message + ANSI_RESET);
        log.warn(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void schedulerInfo(String message) {
        log.info(ANSI_GREEN + message + ANSI_RESET);
        // System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public static void schedulerError(String message) {
        log.error(ANSI_PURPLE + message + ANSI_RESET);
        // System.out.println(ANSI_PURPLE + message + ANSI_RESET);
    }

    @Transactional
    public static void schedulerInfo(SchedulerLogService SchedulerLogService, String SchedulerName, String MessageToLog,
            int TotalTrx) {
        try {
            // Handle null or empty message
            String finalMessage = (MessageToLog == null || MessageToLog.trim().isEmpty())
                    ? "[Info] - Empty Message Detected. Not suppose to happen this."
                    : "[Info] - " + MessageToLog;
            // Log scheduler execution
            SchedulerLog schedulerLog = new SchedulerLog(
                    SchedulerName,
                    finalMessage,
                    TotalTrx);
            SchedulerLogService.saveNewScheduleLog(schedulerLog);
        } catch (org.hibernate.exception.GenericJDBCException e) {
            log.error(
                    "Database error saving scheduler info log for {}: {}. SQLState={}, ErrorCode={}. FALLBACK to file - Name={}, Message={}, TotalTrx={}",
                    SchedulerName, e.getMessage(), e.getSQLState(), e.getErrorCode(),
                    SchedulerName, MessageToLog, TotalTrx, e);

        } catch (Exception e) {
            log.error(
                    "Unexpected error saving scheduler info log for {}: {}. FALLBACK to file - Name={}, Message={}, TotalTrx={}",
                    SchedulerName, e.getMessage(), SchedulerName, MessageToLog, TotalTrx, e);

        }
    }

    public static void schedulerError(SchedulerLogService SchedulerLogService, String SchedulerName,
            String MessageToLog,
            int TotalTrx) {
        try {
            String finalMessage = (MessageToLog == null || MessageToLog.trim().isEmpty())
                    ? "[Error] - Empty Message Detected. Not suppose to happen this."
                    : "[Error] - " + MessageToLog;
            // Log scheduler execution
            SchedulerLog schedulerLog = new SchedulerLog(
                    SchedulerName,
                    finalMessage,
                    TotalTrx);
            SchedulerLogService.saveNewScheduleLog(schedulerLog);
        } catch (org.hibernate.exception.GenericJDBCException e) {
            log.error(
                    "Database error saving scheduler error log for {}: {}. SQLState={}, ErrorCode={}. FALLBACK to file - Name={}, Message={}, TotalTrx={}",
                    SchedulerName, e.getMessage(), e.getSQLState(), e.getErrorCode(),
                    SchedulerName, MessageToLog, TotalTrx, e);

        } catch (Exception e) {
            log.error(
                    "Unexpected error saving scheduler error log for {}: {}. FALLBACK to file - Name={}, Message={}, TotalTrx={}",
                    SchedulerName, e.getMessage(), SchedulerName, MessageToLog, TotalTrx, e);

        }
    }

    public static void SAMLSecurityInfo(String message) {
        String header = ANSI_BLUE_BACKGROUND + ANSI_WHITE + "---- SAML Security Information ----" + ANSI_RESET;
        String footer = ANSI_BLUE_BACKGROUND + ANSI_WHITE + "----------------------------------" + ANSI_RESET;

        log.info(header + "\n" + ANSI_WHITE + message + ANSI_RESET + "\n" + footer);

    }
}