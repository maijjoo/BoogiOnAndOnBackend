package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.dto.ResearchMainDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import com.boogionandon.backend.dto.admin.PredictionResponseDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResearchService {

  // 조사 등록
  public void insertResearch(ResearchMainRequestDTO mainDTO);

  // status needed -> completed 로 변경
  public void updateStatus(Long id);

  //  findByStatusNeededAndSearch 활용해 sevice 만들기
  public Page<ResearchMain> findResearchByStatusNeededAndSearch(String beachSearch, Pageable pageable, Long adminId);

  public Page<ResearchMain> findResearchByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId);

  ResearchMainDetailResponseDTO getResearchDetail(Long researchId);

  List<PredictionResponseDTO> getCollectPrediction(Integer year, Integer month, LocalDate start, LocalDate end);
}
