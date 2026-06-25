package com.example.fms.fms.config;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
@Component
@ConfigurationProperties(prefix = "rms.application")
public class RMSProperties {
    @Value("${rms.application.allowOrigin}")
    private Resource allowOriginFilePath;
    // private String allowOriginFilePath;
    private String[] allowOrigin;
    // @Value("${rms.api.endpoints.file.path}")
    // private Resource endpointsFilePath;
    // private String endpointsFilePath;
    private List<String> apiEndpoints;
    private String GHLServiceID;
    private String GHLPw;
    private String callBackURL;
    private String returnURL;
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
        // try {
        //     //Read the allowOrigin values from the file
        //     List<String> lines = Files.readAllLines(Paths.get(allowOriginFilePath));
            

        //     // Read the API endpoints from the file
        //     this.apiEndpoints = Files.lines(Paths.get(endpointsFilePath))
        //             .map(String::trim)
        //             .collect(Collectors.toList());
        // }
        // catch (IOException e) {
        //     throw new RuntimeException("Failed to load allowOrigin values from file", e);
        // }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(allowOriginFilePath.getInputStream()))) {
            List<String> lines = reader.lines().map(String::trim).collect(Collectors.toList());
            this.allowOrigin = lines.toArray(new String[0]);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load allowOrigin values from file", e);
        }

        // try (BufferedReader reader = new BufferedReader(new InputStreamReader(endpointsFilePath.getInputStream()))) {
        //     this.apiEndpoints = reader.lines().map(String::trim).collect(Collectors.toList());
        // }
        // catch (IOException e) {
        //     throw new RuntimeException("Failed to load apiEndpoints values from file", e);
        // }

    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
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

    public List<String> getApiEndpoints() {
        return apiEndpoints;
    }

    public void setApiEndpoints(List<String> apiEndpoints) {
        this.apiEndpoints = apiEndpoints;
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

    public String[] getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(String[] allowOrigin) {
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
