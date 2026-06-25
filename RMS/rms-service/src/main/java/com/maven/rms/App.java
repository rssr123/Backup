package com.maven.rms;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer for Tomcat WAR deployment
 * Delegates all initialization to SpringBootSecurityApplication
 */
public class App extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// CRITICAL: Initialize log directories before Spring context starts
		// This ensures database logging works in Tomcat deployment
		SpringBootSecurityApplication.initializeLogDirectories();

		return application.sources(SpringBootSecurityApplication.class);
	}
}
