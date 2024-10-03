package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import java.time.LocalDate;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class CleanLocalServiceImplTest {

  @Autowired
  private CleanService cleanService;

  @Test
  @DisplayName("insertClean 메서드 테스트")
  @Commit
  void testInsertClean() {
    CleanRequestDTO cleanDTO = CleanRequestDTO.builder()
        .cleanerUsername("W_testWorker")
        .beachName("해운대해수욕장")
        .realTrashAmount(7)
        .startLatitude(35.15768265599188)
        .startLongitude(129.1572648115502)
        .endLatitude(35.15779193363473)
        .endLongitude(129.15770660944662)
        .mainTrashType("폐어구류")
        .build();

    cleanService.insertClean(cleanDTO);
  }

  // ---------- getTrashDistribution 메서드 테스트 시작 ---------
  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 년")
  void testGetTrashDistributionWithYear() {
    Integer year = 2022;

    TrashMapResponseDTO findTrashDistribution = cleanService.getTrashDistribution(year, null, null, null);

    log.info("findTrashDistribution : " + findTrashDistribution);

  }

  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 년/월")
  void testGetTrashDistributionWithYearAndMonth() {
    Integer year = 2022;
    Integer month = 12;

    TrashMapResponseDTO findTrashDistribution = cleanService.getTrashDistribution(year, month, null, null);

    log.info("findTrashDistribution : " + findTrashDistribution);
  }

  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 시작 ~ 끝")
  void testGetTrashDistributionBetweenStartAndEnd() {

    LocalDate start = LocalDate.of(2023, 6, 1);
    LocalDate end = LocalDate.of(2023, 6, 30);

    TrashMapResponseDTO findTrashDistribution = cleanService.getTrashDistribution(null, null, start, end);

    log.info("findTrashDistribution : " + findTrashDistribution);
  }
  // ---------- getTrashDistribution 메서드 테스트 끝 ---------

  // ---------- getBasicStatistics 메서드 테스트 시작 ---------
  @Test
  @DisplayName("getBasicStatistics 메서드 테스트 - 연도별")
  void testGetBasicStatisticsWithTapCondition1() {
    String tapCondition = "연도별";
    // 생각해 보니 year가 필요가 없고 리포지토리에서 해당년도에 작년 -4 ~ 작년 까지 보여줌 year 필요 없음
//    Integer year = 2022;
    String beachName = "해운대해수욕장";

//    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, null, null, beachName);
    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, null, null, null);

    log.info("findBasicStatistics : " + findBasicStatistics);
  }
  @Test
  @DisplayName("getBasicStatistics 메서드 테스트 - 월별")
  void testGetBasicStatisticsWithTapCondition2() {
    String tapCondition = "월별";
    Integer year = 2022;
    // 생각해 보니 month도 여기서 필요가 없음, 해당 연도의 모든 month를 사용할테니
    Integer month = 12;
    String beachName = "해운대해수욕장";


    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, null, beachName);
//    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, null, null, null);

    log.info("findBasicStatistics : " + findBasicStatistics);

  }
  @Test
  @DisplayName("getBasicStatistics 메서드 테스트 - 일별")
  void testGetBasicStatisticsWithTapCondition3() {
    String tapCondition = "일별";
    Integer year = 2023;
    Integer month = 3;
    String beachName = "해운대해수욕장";

    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, month, beachName);
//    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, 2022, null, null);

    log.info("findBasicStatistics : " + findBasicStatistics);
  }
  // ---------- getBasicStatistics 메서드 테스트 끝 ---------
  // ----------- findResearchByStatusNeededAndSearch 테스트 시작 -----------
  @Test
  @DisplayName("findResearchByStatusNeededAndSearch 테스트")
  void testFindResearchByStatusNeededAndSearch() {
    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("cleanDateTime").descending() :
            Sort.by("cleanDateTime").ascending()
    );

    Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(beachSearch, pageable);
//    Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(null, pageable);

    log.info("findList : " + findList);
    log.info("findList : " + findList.getContent());
  }
  // ----------- findResearchByStatusNeededAndSearch 테스트 끝 -----------
}