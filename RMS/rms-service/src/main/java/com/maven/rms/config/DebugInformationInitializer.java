package com.maven.rms.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maven.rms.utils.DebugInformation;

import javax.annotation.PostConstruct;

@Component
public class DebugInformationInitializer {

    @Value("${logging.custom.exception.debuginformation.stacktrack.suppress:false}")
    private boolean suppressStacktrace;

    @PostConstruct
    public void init() {
        DebugInformation.suppressStacktrace = suppressStacktrace;
    }
}
