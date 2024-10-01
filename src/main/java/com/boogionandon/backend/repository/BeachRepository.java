package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Beach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeachRepository extends JpaRepository<Beach, String> {

}
