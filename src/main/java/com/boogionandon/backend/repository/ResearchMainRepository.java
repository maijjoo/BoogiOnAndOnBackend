package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.repository.queryDSL.ResearchMainRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResearchMainRepository extends JpaRepository<ResearchMain, Long>, ResearchMainRepositoryCustom {

  // 일단 여기서는 researchSubList 빼고 가져오고
  // researchSubList는 따로 불러와서 합치기 -> researchSubList, images 두개가 같이 안됨
  @Query("select distinct rm from ResearchMain rm "
      + "left  join fetch rm.researcher r "
      + "left join fetch rm.images "
      + "where rm.id = :researchId")
  Optional<ResearchMain> findByIdWithOutSub(@Param("researchId") Long researchId);
}
