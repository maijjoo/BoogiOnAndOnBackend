package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.Clean;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CleanRepositoryCustom {

  List<Clean> findByDateCriteria(Integer year, Integer month, LocalDate start, LocalDate end);

  List<Clean> getBasicStatistics(String tapCondition, Integer year, Integer month, String beachName);

  Page<Clean> findByStatusNeededAndSearch(String beachSearch, Pageable pageable);
}
