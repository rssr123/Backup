package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/saml")
@Slf4j
public class SAMLLogoutController {

    @Value("${rms.application.backPortalURL}")
    private String angularOnlinePortalURL;

    @Value("${spring.security.saml2.relyingparty.registration.ssm.singlelogout.url:}")
    private String idpLogoutUrl;

    /**
     * Main logout endpoint - works with your existing auth.service.ts
     * This clears the application session and returns success
     * Your frontend then handles IDP logout via iframe if needed
     */
    @PostMapping("/logout")
    public ResponseEntity<String> samlLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("SAML logout initiated for user: {}",
                (authentication != null ? authentication.getName() : "anonymous"));

        try {
            // Clear Spring Security context
            if (authentication != null) {
                SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                logoutHandler.logout(request, response, authentication);
            }

            // Clear security context
            SecurityContextHolder.clearContext();

            // Invalidate session
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            log.info("Application logout completed successfully");
            return ResponseEntity.ok("Logout successful");

        } catch (Exception e) {
            log.error("Error during SAML logout process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during logout");
        }
    }

    /**
     * Returns the IDP logout URL for your iframe approach
     * Your frontend can use this in environment.idpLogoutEndpoint
     */
    @GetMapping("/logout/idp-url")
    public ResponseEntity<String> getIdpLogoutUrl() {
        log.info("IDP logout URL requested");

        if (idpLogoutUrl == null || idpLogoutUrl.isEmpty()) {
            return ResponseEntity.ok(""); // Empty for local environment or when not configured
        }

        return ResponseEntity.ok(idpLogoutUrl);
    }

    /**
     * Handle SAML Single Logout callbacks from IDP
     */
    @PostMapping("/slo")
    public ResponseEntity<String> handleSingleLogout(HttpServletRequest request, HttpServletResponse response) {
        log.info("SAML Single Logout (SLO) callback received");

        try {
            // Clear any remaining authentication
            SecurityContextHolder.clearContext();

            // Invalidate session if exists
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            log.info("SAML SLO processed successfully");
            return ResponseEntity.ok("Logout successful");

        } catch (Exception e) {
            log.error("Error processing SAML SLO", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing logout");
        }
    }

    /**
     * Additional cleanup endpoint if needed
     */
    @PostMapping("/logout/complete")
    public ResponseEntity<String> completeLogout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Final logout completion called");

        try {
            // Ensure complete cleanup
            SecurityContextHolder.clearContext();
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            log.info("Final logout completed successfully");
            return ResponseEntity.ok("Final logout successful");

        } catch (Exception e) {
            log.error("Error during final logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during final logout");
        }
    }
}
