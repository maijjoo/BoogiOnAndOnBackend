package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.dto.CleanDetailResponseDTO;
import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class CleanLocalServiceImplTest {

  @Autowired
  private CleanService cleanService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BeachRepository beachRepository;

  @Test
  @DisplayName("insertClean 메서드 테스트")
  @Commit
  void testInsertClean() {

    Random random = new Random();

    String cleanerUsername = "W_testWorker";

    Worker findCleaner = (Worker) memberRepository.findByUsernameWithDetails(cleanerUsername)
        .orElseThrow(() -> new UsernameNotFoundException("Worker with username "+ cleanerUsername +" not found"));

    Admin managedAdmin = (Admin) memberRepository.findById(findCleaner.getManagerId())
        .orElseThrow(() -> new NoSuchElementException("Admin with id "+ findCleaner.getManagerId() +" not found"));

    List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();

    Beach randomBeach = beachRepository.findById(assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(random.nextInt(assignmentAreaList.size()))));

    List<String> beforeUploadedFileNames = Arrays.asList("B_20241006005731_test.jpeg", "B_20241006005731_test1.jpeg");
    List<String> afterUploadedFileNames = Arrays.asList("A_20241006005731_test.jpeg", "A_20241006005731_test1.jpeg", "A_20241006005731_test2.jpeg");

    CleanRequestDTO cleanDTO = CleanRequestDTO.builder()
        .cleanerUsername(cleanerUsername)
        .beachName(randomBeach.getBeachName())
        .realTrashAmount(7)
        .startLatitude(35.15768265599188)
        .startLongitude(129.1572648115502)
        .endLatitude(35.15779193363473)
        .endLongitude(129.15770660944662)
        .mainTrashType("폐어구류")
        .beforeUploadedFileNames(beforeUploadedFileNames)
        .afterUploadedFileNames(afterUploadedFileNames)
        .build();

    cleanService.insertClean(cleanDTO);
  }

  // ---------- getTrashDistribution 메서드 테스트 시작 ---------
  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 년")
  void testGetTrashDistributionWithYear() {
    Integer year = 2022;

    List<TrashMapResponseDTO> findTrashDistribution = cleanService.getTrashDistribution(year, null, null, null);

    log.info("findTrashDistribution : " + findTrashDistribution);
    log.info("findTrashDistribution.size() : " + findTrashDistribution.size());

  }

  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 년/월")
  void testGetTrashDistributionWithYearAndMonth() {
    Integer year = 2022;
    Integer month = 11;

    List<TrashMapResponseDTO> findTrashDistribution = cleanService.getTrashDistribution(year, month, null, null);

    log.info("findTrashDistribution : " + findTrashDistribution);
    log.info("findTrashDistribution.size() : " + findTrashDistribution.size());
  }

  @Test
  @DisplayName("getTrashDistribution 메서드 테스트 - 시작 ~ 끝")
  void testGetTrashDistributionBetweenStartAndEnd() {

    LocalDate start = LocalDate.of(2023, 3, 1);
    LocalDate end = LocalDate.of(2023, 6, 30);

    List<TrashMapResponseDTO> findTrashDistribution = cleanService.getTrashDistribution(null, null, start, end);

    log.info("findTrashDistribution : " + findTrashDistribution);
    log.info("findTrashDistribution.size() : " + findTrashDistribution.size());

  }
  // ---------- getTrashDistribution 메서드 테스트 끝 ---------

  // ---------- getBasicStatistics 메서드 테스트 시작 ---------
  @Test
  @DisplayName("getBasicStatistics 메서드 테스트 - 연도별")
  void testGetBasicStatisticsWithTapCondition1() {
    String tapCondition = "연도별";
    // 생각해 보니 year가 필요가 없고 리포지토리에서 해당년도에 작년 -4 ~ 작년 까지 보여줌 year 필요 없음
//    Integer year = 2022;
    String beachName = "광안리해수욕장";

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
    String beachName = "광안리해수욕장";


//    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, null, beachName);
    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, null, null, null);

    log.info("findBasicStatistics : " + findBasicStatistics);

  }
  @Test
  @DisplayName("getBasicStatistics 메서드 테스트 - 일별")
  void testGetBasicStatisticsWithTapCondition3() {
    String tapCondition = "일별";
    Integer year = 2022;
    Integer month = 3;
    String beachName = "광안리해수욕장";

//    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, month, beachName);
    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, month, null);

    log.info("findBasicStatistics : " + findBasicStatistics);
  }
  // ---------- getBasicStatistics 메서드 테스트 끝 ---------
  // ----------- testFindCleanByStatusNeededAndSearch, testFindCleanByStatusCompletedAndSearch 테스트 시작 -----------
  @Test
  @DisplayName("findResearchByStatusNeededAndSearch 테스트")
  void testFindCleanByStatusNeededAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 8L;

    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("cleanDateTime").descending() :
            Sort.by("cleanDateTime").ascending()
    );

    Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(beachSearch, pageable, adminId);
//    Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(null, pageable, adminId);

    log.info("findList : " + findList);
    log.info("findList : " + findList.getContent());
  }
  @Test
  @DisplayName("testFindCleanByStatusCompletedAndSearch 테스트")
  void testFindCleanByStatusCompletedAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 8L;

    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("cleanDateTime").descending() :
            Sort.by("cleanDateTime").ascending()
    );

//    Page<Clean> findList = cleanService.findResearchByStatusCompletedAndSearch(beachSearch, pageable, adminId);
    Page<Clean> findList = cleanService.findResearchByStatusCompletedAndSearch(null, pageable, adminId);

    log.info("findList : " + findList);
    log.info("findList : " + findList.getContent());
  }
  // ----------- testFindCleanByStatusNeededAndSearch, testFindCleanByStatusCompletedAndSearch 테스트 끝 -----------
  // ----------- getCleanDetail 테스트 시작 -----------
  @Test
  @DisplayName("getCleanDetail 테스트")
  void testGetCleanDetail() {
    Long cleanId = 1L;

    CleanDetailResponseDTO cleanDTO = cleanService.getCleanDetail(cleanId);

    log.info("cleanDTO : " + cleanDTO);
  }
  // ----------- getCleanDetail 테스트 끝 -----------
}