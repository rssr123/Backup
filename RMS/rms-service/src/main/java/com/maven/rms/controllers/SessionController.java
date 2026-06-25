package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/session/v1")
@Slf4j
public class SessionController {

    @GetMapping("/status")
    public ResponseEntity<String> checkSession(HttpServletRequest request) {

        return request.getSession(false) != null
                ? ResponseEntity.ok("ACTIVE")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("EXPIRED");
    }
}
