package com.maven.rms.services;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.AuthToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthTokenService {
	@Autowired
	private AuthService aSvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRandom random = new SecureRandom();

    public AuthToken createToken(String username, Duration ttl, String jsess, String nonce, String ua, String ip) {
        byte[] b = new byte[32];
        random.nextBytes(b);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        long expiry = System.currentTimeMillis() + ttl.toMillis();
        AuthToken tokenObj = new AuthToken(token, username, expiry, jsess, nonce, ua, ip);
        String serialObj = AuthTokenService.serialize(tokenObj);
        //System.out.println("=====TokenGen=====");
        //System.out.println(serialObj);
        //System.out.println("=====TokenGen=====");
        //log.error("[createToken] IP: " + ip);
        aSvc.updateUserSessionId(serialObj);
        return tokenObj;
    }

    public AuthToken getToken(Object sessionToken) {
    	AuthToken t = sessionToken != null ? (sessionToken instanceof AuthToken ? ((AuthToken) sessionToken) : null) : null;
    	if(t == null) 
	        t = AuthTokenService.deserialize(aSvc.getUserLatesttToken());
        return t;
    }

    public void revoke() { 
		aSvc.updateUserSessionId(null);
    }

    public static String serialize(AuthToken token) {
        try {
            return objectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing AuthToken", e);
        }
    }

    public static AuthToken deserialize(String json) {
        try {
        	if(json == null)
        		 return null;
            return objectMapper.readValue(json, AuthToken.class);
        } catch (Exception e) {
            //throw new RuntimeException("Error deserializing AuthToken", e);
        	return null;
        }
    }
}