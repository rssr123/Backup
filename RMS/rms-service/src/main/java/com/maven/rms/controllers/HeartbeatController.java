package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.services.AuthService;

@RestController
@RequestMapping("/api")
public class HeartbeatController {

    @Autowired
    private AuthService authService;

    @GetMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(HttpServletRequest request) {
        // OPTIONAL: rely on Spring Security to authenticate this endpoint.
        // If you want to keep your custom check, leave the next 3 lines.
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        session.setAttribute("hb", System.currentTimeMillis());
        return ResponseEntity.noContent().build();
    }


}
 