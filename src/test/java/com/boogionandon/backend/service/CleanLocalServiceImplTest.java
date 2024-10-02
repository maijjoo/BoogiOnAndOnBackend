package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import java.time.LocalDate;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

}