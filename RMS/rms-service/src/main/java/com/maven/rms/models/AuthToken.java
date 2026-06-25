package com.maven.rms.models;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class AuthToken implements Serializable{
    private String token;
    private String username;
    private long expiryEpochMs;
    private String clientFingerprint;
    private Boolean useIp;
    //private String nonce;
    //private String JsessionId;
    private String ip;
    private String ua;
    
    public AuthToken() {}
    
    public AuthToken(String token, String username, long expiry, String JsessionId, 
    				String nonce, String ua, String ip) {
    	this.token = token;
    	this.username = username;
    	this.expiryEpochMs = expiry;
    	//this.JsessionId = JsessionId;
        //this.nonce = nonce;
        this.ua = ua;
        this.ip = ip;
       	this.useIp = nonce == null ? true : false; //true;
    	this.clientFingerprint = fingerprint(nonce, ua, ip, useIp);
        
    }
    
    public static String fingerprint(String nonce, String ua, String ip, Boolean useIp) {
	    String fingerprintString  = (ua == null ? "" : ua) + "|" + (useIp ? (ip == null ? "" : ip) : (nonce != null ? nonce : ""));
//	    System.out.println("FingerPrint Generated using: " + fingerprintString);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().withoutPadding().encodeToString(md.digest(fingerprintString.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

}
