package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.repository.queryDSL.ResearchMainRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchMainRepository extends JpaRepository<ResearchMain, Long>, ResearchMainRepositoryCustom {

}
