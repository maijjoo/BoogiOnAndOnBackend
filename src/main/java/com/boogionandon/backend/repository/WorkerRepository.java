package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Worker;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
}
