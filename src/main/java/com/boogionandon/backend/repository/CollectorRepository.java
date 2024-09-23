package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Collector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectorRepository extends JpaRepository<Collector, Long> {

}
