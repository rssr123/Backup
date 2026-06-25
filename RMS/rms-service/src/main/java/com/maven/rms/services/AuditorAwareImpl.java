package com.maven.rms.services;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // will need implement this later
        // Fetch the logged-in user's name
		//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//return (authentication == null || !authentication.isAuthenticated())?
		//		null : Optional.ofNullable(authentication.getName());
		
        return Optional.ofNullable("Brian");
    }
}
