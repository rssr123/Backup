package com.maven.rms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(
		prePostEnabled = true, 
		securedEnabled = true, 
		jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		String hierarchy = "ROLE_SUPERADMIN > ROLE_REQUESTORHOD\n"
				+ "ROLE_REQUESTORHOD > ROLE_FINANCEHOD\n"
				+ "ROLE_FINANCEHOD > ROLE_ADMIN\n"
				+ "ROLE_ADMIN > ROLE_FINANCEADMIN\n"
				+ "ROLE_FINANCEADMIN > ROLE_AUDITOR\n"
				+ "ROLE_AUDITOR > ROLE_REQUESTER\n"
				+ "ROLE_REQUESTER > ROLE_VERIFIED_CUSTOMER\n"
				+ "ROLE_VERIFIED_CUSTOMER > ROLE_GENERAL_USER";
		roleHierarchy.setHierarchy(hierarchy);
		
        final DefaultMethodSecurityExpressionHandler handler = (DefaultMethodSecurityExpressionHandler) super.createExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }	
}