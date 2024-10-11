package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.dto.CleanDetailResponseDTO;
import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CleanService {

  // 클린 보고서를 insert 시키는 메서드
  void insertClean(CleanRequestDTO cleanRequestDTO);

  // 관리자화면에서 보는 쓰레기 분포도에서 사용할 메서드
  List<TrashMapResponseDTO> getTrashDistribution(Integer year, Integer month, LocalDate start, LocalDate end);

  // 관리자 화면에서 보는 기초통계에서 사용할 메서드
  BasicStatisticsResponseDTO getBasicStatistics(String tapCondition, Integer year, Integer month, String beachName);

  // 관리자 화면에서 보는 new, closed 작업에 관한 메서드
  Page<Clean> findResearchByStatusNeededAndSearch(String beachSearch, Pageable pageable, Long adminId);
  Page<Clean> findResearchByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId);

  CleanDetailResponseDTO getCleanDetail(Long cleanId);

  void updateStatus(Long cleanId);
}
