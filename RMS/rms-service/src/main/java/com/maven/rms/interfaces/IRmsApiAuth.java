package com.maven.rms.interfaces;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;


import com.maven.rms.models.RmsApiAuth;

public interface IRmsApiAuth extends JpaRepository<RmsApiAuth, Long> {
    Optional<RmsApiAuth> findByNmAndPwAndStatus(String nm, String pw, String status);
}