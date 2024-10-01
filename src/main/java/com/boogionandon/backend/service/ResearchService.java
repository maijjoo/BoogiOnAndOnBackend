package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import org.springframework.transaction.annotation.Transactional;

public interface ResearchService {

  // 조사 등록
  public void insertResearch(ResearchMainRequestDTO mainDTO);


}
