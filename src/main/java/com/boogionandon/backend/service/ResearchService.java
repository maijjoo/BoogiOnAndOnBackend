package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.ResearchMainDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchMainListResponseDTO;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ResearchService {

  // 조사 등록
  public void insertResearch(ResearchMainRequestDTO mainDTO);

  // status needed -> completed 로 변경
  public void updateStatus(Long id);

  //  findByStatusNeededAndSearch 활용해 sevice 만들기
  public Page<ResearchMain> findResearchByStatusNeededAndSearch(String beachSearch, Pageable pageable, Long adminId);

  public Page<ResearchMain> findResearchByStatusCompletedAndSearch(String beachSearch, Pageable pageable);

  ResearchMainDetailResponseDTO getResearchDetail(Long researchId);
}
