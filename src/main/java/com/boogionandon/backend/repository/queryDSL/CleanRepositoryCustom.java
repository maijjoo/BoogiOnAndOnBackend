package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.Clean;
import java.time.LocalDate;
import java.util.List;

public interface CleanRepositoryCustom {

  List<Clean> findByDateCriteria(Integer year, Integer month, LocalDate start, LocalDate end);
}
