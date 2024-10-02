package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.repository.queryDSL.CleanRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanRepository extends JpaRepository<Clean, Long>, CleanRepositoryCustom {

}
