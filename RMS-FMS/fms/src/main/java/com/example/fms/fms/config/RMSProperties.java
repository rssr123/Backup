package com.example.fms.fms.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rms.application")
public class RMSProperties {

    @Value("${rms.application.allowOrigin}")
    private String allowOriginString;
    @Value("classpath:allow-origin.txt")
    private File originFile;
    private List<String> allowOrigin;
    private String GHLServiceID;
    private String GHLPw;
    private String callBackURL;
    private String SAMLSecurityConfigPort;
    private String onlinePortalURL;
    private String backPortalURL; // Added
    private boolean auditlogEnable; // Added
    private boolean sendEmailEnable; // Added
    private String emailTo; // Added
    private String[] emailCC; // Added
    private String WriteFilePath;
    private Long WriteFileMaxFileSizeInMB;
    private Integer WriteFileMaxFileRolling;
    private boolean WriteToFileEnable;
    private String LogLevel;
    
    @PostConstruct
    private void init() {
    	if(!allowOriginString.contains("classpath:"))
    		this.allowOrigin = Arrays.asList(allowOriginString.split(","));
    	else {
			try {
				Scanner input = new Scanner(originFile);
		    	while (input.hasNextLine())
		    	   allowOrigin.add(input.nextLine());
		    	input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    }

    public String getLogLevel() {
        return LogLevel;
    }

    public void setLogLevel(String logLevel) {
        LogLevel = Optional.ofNullable(logLevel)
                .map(String::toUpperCase)
                .orElse(null);
        ;
    }

    public boolean getWriteToFileEnable() {
        return WriteToFileEnable;
    }

    public void setWriteToFileEnable(boolean writeToFileEnable) {
        WriteToFileEnable = writeToFileEnable;
    }

    public String getWriteFilePath() {
        return WriteFilePath;
    }

    public void setWriteFilePath(String writeFilePath) {
        WriteFilePath = writeFilePath;
    }

    public Long getWriteFileMaxFileSizeInMB() {
        return WriteFileMaxFileSizeInMB;
    }

    public void setWriteFileMaxFileSizeInMB(Long writeFileMaxFileSizeInMB) {
        WriteFileMaxFileSizeInMB = writeFileMaxFileSizeInMB;
    }

    public Integer getWriteFileMaxFileRolling() {
        return WriteFileMaxFileRolling;
    }

    public void setWriteFileMaxFileRolling(Integer writeFileMaxFileRolling) {
        WriteFileMaxFileRolling = writeFileMaxFileRolling;
    }

    public String getBackPortalURL() {
        return backPortalURL;
    }

    public void setBackPortalURL(String backPortalURL) {
        this.backPortalURL = backPortalURL;
    }

    public boolean isAuditlogEnable() {
        return auditlogEnable;
    }

    public void setAuditlogEnable(boolean auditlogEnable) {
        this.auditlogEnable = auditlogEnable;
    }

    public boolean isSendEmailEnable() {
        return sendEmailEnable;
    }

    public void setSendEmailEnable(boolean sendEmailEnable) {
        this.sendEmailEnable = sendEmailEnable;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String[] getEmailCC() {
        return emailCC;
    }

    public void setEmailCC(String[] emailCC) {
        this.emailCC = emailCC;
    }

    public String getOnlinePortalURL() {
        return onlinePortalURL;
    }

    public void setOnlinePortalURL(String onlinePortalURL) {
        this.onlinePortalURL = onlinePortalURL;
    }

    public String getSAMLSecurityConfigPort() {
        return SAMLSecurityConfigPort;
    }

    public void setSAMLSecurityConfigPort(String sAMLSecurityConfigPort) {
        SAMLSecurityConfigPort = sAMLSecurityConfigPort;
    }

    public String getCallBackURL() {
        return callBackURL;
    }

    public void setCallBackURL(String callBackURL) {
        this.callBackURL = callBackURL;
    }

    public List<String> getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(List<String> allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public String getGHLServiceID() {
        return GHLServiceID;
    }

    public void setGHLServiceID(String GHLServiceID) {
        this.GHLServiceID = GHLServiceID;
    }

    public String getGHLPw() {
        return GHLPw;
    }

    public void setGHLPw(String GHLPw) {
        this.GHLPw = GHLPw;
    }

    public String getcallBackURL() {
        return callBackURL;
    }

    public void setcallBackURL(String callBackURL) {
        this.callBackURL = callBackURL;
    }
}
