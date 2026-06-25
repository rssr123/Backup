package com.maven.rms.utils;
import org.springframework.session.Session;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HazelcastSessionUtil {
	
    private final Hazelcast4IndexedSessionRepository sessionRepository;
    
    public HazelcastSessionUtil(Hazelcast4IndexedSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    public void save(Session session) {
        try {
            // The private inner class
            Class<?> hazelcastSessionClass = Class.forName(
                Hazelcast4IndexedSessionRepository.class.getName() + "$HazelcastSession"
            );

            // Grab the save(HazelcastSession) method
            Method SAVE_METHOD = Hazelcast4IndexedSessionRepository.class
                    .getDeclaredMethod("save", hazelcastSessionClass);
            SAVE_METHOD.setAccessible(true);
        	SAVE_METHOD.invoke(sessionRepository, session);

        } catch (Exception e) {
            throw new IllegalStateException("Could not save session to Hazelcast", e);
        }
    }
    
    public static String decodeCookieSessionId(String cookieValue) {
    	try {
    		byte[] decodedBytes = Base64.getDecoder().decode(cookieValue);
    		return new String(decodedBytes, StandardCharsets.UTF_8);
    	}catch(IllegalArgumentException | NullPointerException e) {
    		return cookieValue;
    	}
    }   
    
    public static String encodeToCookieSessionId(String rawId) {
    	try {
    		return Base64.getEncoder().encodeToString(rawId.getBytes(StandardCharsets.UTF_8));
    	}catch(IllegalArgumentException | NullPointerException e) {
    		return rawId;
    	}
    }
}
