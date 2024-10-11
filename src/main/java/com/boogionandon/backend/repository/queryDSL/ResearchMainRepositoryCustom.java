package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.ResearchMain;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResearchMainRepositoryCustom {

  Page<ResearchMain> findByStatusNeededAndSearchForSuper(String beachSearch, Pageable pageable);
  Page<ResearchMain> findByStatusNeededAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId);

  Page<ResearchMain> findByStatusCompletedAndSearchForSuper(String beachSearch, Pageable pageable);
  Page<ResearchMain> findByStatusCompletedAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId);

  // 수거 예측량을 보기 위한 - research 보고서를 바탕으로 만들어짐
  List<ResearchMain> findByDateCriteria(Integer year, Integer month, LocalDate start, LocalDate end);
}
