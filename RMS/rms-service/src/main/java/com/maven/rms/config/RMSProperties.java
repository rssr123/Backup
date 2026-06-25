package com.maven.rms.config;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.maven.rms.models.WhiteList;
import com.maven.rms.services.CommonService;

import org.springframework.beans.factory.annotation.Autowired;
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
    // @Value("${rms.application.allowOrigin}")
    // private Resource allowOriginFilePath;
    // private String allowOriginFilePath;
    private String[] allowOrigin;
    @Value("${rms.api.endpoints.file.path}")
    private Resource endpointsFilePath;
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

    @Value("${quartz.enable.tasks}")
    private String quartzEnableStatus;

    public String getQuartzEnableStatus() {
        return quartzEnableStatus;
    }

    public void setQuartzEnableStatus(String quartzEnableStatus) {
        this.quartzEnableStatus = quartzEnableStatus;
    }

    @Value("${rms.env.javaUrl}")
    private String javaUrl;

    @Autowired
    private CommonService commonService; // <<== Inject your service to call sp_getwhitelistip()

    @PostConstruct
    private void init() {
        // try {
        // //Read the allowOrigin values from the file
        // List<String> lines = Files.readAllLines(Paths.get(allowOriginFilePath));

        // // Read the API endpoints from the file
        // this.apiEndpoints = Files.lines(Paths.get(endpointsFilePath))
        // .map(String::trim)
        // .collect(Collectors.toList());
        // }
        // catch (IOException e) {
        // throw new RuntimeException("Failed to load allowOrigin values from file", e);
        // }

        // try (BufferedReader reader = new BufferedReader(new
        // InputStreamReader(allowOriginFilePath.getInputStream()))) {
        // List<String> lines =
        // reader.lines().map(String::trim).collect(Collectors.toList());
        // this.allowOrigin = lines.toArray(new String[0]);
        // } catch (IOException e) {
        // throw new RuntimeException("Failed to load allowOrigin values from file", e);
        // }

        loadAllowOriginFromDatabase();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(endpointsFilePath.getInputStream()))) {
            this.apiEndpoints = reader.lines().map(String::trim).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load apiEndpoints values from file", e);
        }

    }

    private void loadAllowOriginFromDatabase() {
        List<WhiteList> whiteListEntries = commonService.sp_getwhitelistip();

        this.allowOrigin = whiteListEntries.stream()
                .map(WhiteList::getIp)
                .map(String::trim)
                .toArray(String[]::new);
    }

    public String getJavaUrl() {
        return javaUrl;
    }

    public void setJavaUrl(String javaUrl) {
        this.javaUrl = javaUrl;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public List<String> getApiEndpoints() {
        return apiEndpoints;
    }

    public void setApiEndpoints(List<String> apiEndpoints) {
        this.apiEndpoints = apiEndpoints;
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
