package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.enums.ReportStatus;
import java.util.List;

public interface ResearchMainRepositoryCustom {

  List<ResearchMain> findByStatusNeededAndSearch(String search);

}
