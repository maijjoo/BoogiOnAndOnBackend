package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.dto.PageRequestDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResearchMainRepositoryCustom {

  Page<ResearchMain> findByStatusNeededAndSearch(String beachSearch, Pageable pageable);

}
