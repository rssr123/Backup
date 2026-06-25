package com.maven.rms.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.SchedulerLog;


@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long> {
	Optional<SchedulerLog> findSchedulerLogBySchLogId(Long id);
}
