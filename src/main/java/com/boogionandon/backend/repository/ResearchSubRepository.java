package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.ResearchSub;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResearchSubRepository extends JpaRepository<ResearchSub, Long> {

  @Query("select rs from ResearchSub rs "
      + "where rs.research.id = :researchId")
  List<ResearchSub> findListByMainId(@Param("researchId") Long researchId);
}
