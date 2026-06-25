package com.maven.rms.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class Common {

    public static String hashStringWithSHA256(String input) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Update the digest with the input string bytes
            md.update(input.getBytes());

            // Get the hashed bytes
            byte[] hashedBytes = md.digest();

            // Convert the hashed bytes to a hexadecimal representation
            String hashedString = DatatypeConverter.printHexBinary(hashedBytes).toLowerCase();

            return hashedString;
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception or log an error
            e.printStackTrace();
            return null;
        }
    }
    
}
