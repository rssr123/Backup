package com.maven.rms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
	
	
	Optional<Email> findEmailByEmailId(Long emailId);
	
	List<Email> findAllEmailByStatus(String status);
}
