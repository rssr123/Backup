package com.maven.rms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = 1200) // 20 min session timeout
public class SessionConfig {
}