package com.example.fms.fms.utils;

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
        System.out.println(ANSI_BLUE + message + ANSI_RESET);
    }

    public static void error(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    public static void warning(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void schedulerInfo(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public static void schedulerError(String message) {
        System.out.println(ANSI_PURPLE + message + ANSI_RESET);
    }

    public static void SAMLSecurityInfo(String message) {
        String header = ANSI_BLUE_BACKGROUND + ANSI_WHITE + "---- SAML Security Information ----" + ANSI_RESET;
        String footer = ANSI_BLUE_BACKGROUND + ANSI_WHITE + "----------------------------------" + ANSI_RESET;

        System.out.println(header);
        System.out.println(ANSI_WHITE + message + ANSI_RESET);
        System.out.println(footer);
    }

}
