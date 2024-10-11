package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.ResearchMainDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import com.boogionandon.backend.dto.ResearchSubRequestDTO;
import com.boogionandon.backend.dto.admin.PredictionResponseDTO;
import com.boogionandon.backend.repository.MemberRepository;
import java.time.LocalDate;
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
class ResearchLocalServiceImplTest {

  @Autowired
  private ResearchService researchService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("insertResearch 메서드 테스트")
  @Commit
  void testInsertResearch() {

    Random random = new Random();

    String researcherUsername = "W_testWorker";

    Worker researcher = (Worker) memberRepository.findByUsernameWithDetails(researcherUsername)
        .orElseThrow(() -> new UsernameNotFoundException("Worker with username "+ researcherUsername +" not found"));

    Admin managedAdmin = (Admin) memberRepository.findById(researcher.getManagerId())
        .orElseThrow(() -> new NoSuchElementException("Admin with id "+ researcher.getManagerId() +" not found"));

    String randpmBeachName = managedAdmin.getAssignmentAreaList().get(random.nextInt(managedAdmin.getAssignmentAreaList().size()));

    LocalDate currentDate = LocalDate.now();;

    ResearchMainRequestDTO mainDTO = ResearchMainRequestDTO.builder()
        .researcherUsername(researcherUsername)
        .beachName(randpmBeachName)
        .expectedTrashAmount(180)
        .weather("소나기") // 당일 날씨
        .specialNote("태풍")  // 쓰레기가 영향을 받았을 것 같은 이상기후
        // 이미지는 생략
        .build();

    for (int i = 1; i <= 4; i++) {
      ResearchSubRequestDTO subDTO = ResearchSubRequestDTO.builder()
          .beachNameWithIndex(mainDTO.getBeachName() + i)
          // 반복으로 하니까 위, 경도 바꾸기가 쉽지않아 일단 동일하게 받음
          .startLatitude(35.15768265599188)
          .startLongitude(129.1572648115502)
          .endLatitude(35.15779193363473)
          .endLongitude(129.15770660944662)
          .mainTrashType("폐어구류")
          // researchLength는 실제로 넣을 때는 위의 위,경도를 계산해서 넣기
          .build();

      mainDTO.getResearchSubList().add(subDTO);
    }
    researchService.insertResearch(mainDTO);
  }

  // ----------- findByStatusChange 테스트 시작 -----------
  @Test
  @DisplayName("findByStatusChange 테스트")
  @Commit
  void testFindByStatusChange() {
    Long researchId = 2L;
    // 따로 값이 필요 없다고 판단 status 파라미터 같은거

    researchService.updateStatus(researchId);
  }
  // ----------- findByStatusChange 테스트 끝 -----------
  // ----------- findResearchByStatusNeededAndSearch, findResearchByStatusCompletedAndSearch 테스트 시작 -----------
  @Test
  @DisplayName("findResearchByStatusNeededAndSearch 테스트")
  void testFindResearchByStatusNeededAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 1L;

    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("reportTime").descending() :
            Sort.by("reportTime").ascending()
    );

    Page<ResearchMain> findList = researchService.findResearchByStatusNeededAndSearch(beachSearch, pageable, adminId);
//    Page<ResearchMain> findList = researchService.findResearchByStatusNeededAndSearch(null, pageable, adminId);

    log.info("findList : " + findList.getTotalElements());
    log.info("findList : " + findList.getContent());
  }
  @Test
  @DisplayName("findResearchByStatusCompletedAndSearch 테스트")
  void testFindResearchByStatusCompletedAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L, 8L, 9L initData 에서 자동으로 만들어진 regular
    Long adminId = 8L;

    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("reportTime").descending() :
            Sort.by("reportTime").ascending()
    );

//    Page<ResearchMain> findList = researchService.findResearchByStatusCompletedAndSearch(beachSearch, pageable, adminId);
    Page<ResearchMain> findList = researchService.findResearchByStatusCompletedAndSearch(null, pageable, adminId);

    log.info("findList : " + findList);
    log.info("findList : " + findList.getContent());
  }
  // ----------- findResearchByStatusNeededAndSearch 테스트 끝 -----------
  // ----------- getResearchDetail 테스트 시작 -----------
  @Test
  @DisplayName("getResearchDetail 메서드 테스트")
  void testGetResearchDetail() {

    Long researchId = 2L;

    ResearchMainDetailResponseDTO findMain = researchService.getResearchDetail(researchId);
    log.info("findMain : " + findMain.toString());
  }

  // ----------- getResearchDetail 테스트 끝 -----------
  // ---------- getCollectPrediction 메서드 테스트 시작 ---------
  @Test
  @DisplayName("getCollectPrediction 메서드 테스트 - 년")
  void testGetCollectPredictionWithYear() {
    Integer year = 2022;

    List<PredictionResponseDTO> findCollectPrediction = researchService.getCollectPrediction(year, null,
        null, null);

    log.info("findCollectPrediction : " + findCollectPrediction);
    log.info("findCollectPrediction : " + findCollectPrediction.size());

  }

  @Test
  @DisplayName("getCollectPrediction 메서드 테스트 - 년/월")
  void testGetCollectPredictionWithYearAndMonth() {
    Integer year = 2022;
    Integer month = 2;

    List<PredictionResponseDTO> findCollectPrediction = researchService.getCollectPrediction(year,
        month, null, null);

    log.info("findCollectPrediction : " + findCollectPrediction);
    log.info("findCollectPrediction : " + findCollectPrediction.size());

  }

  @Test
  @DisplayName("getCollectPrediction 메서드 테스트 - 시작 ~ 끝")
  void testGetCollectPredictionBetweenStartAndEnd() {

    LocalDate start = LocalDate.of(2023,1,5);
    LocalDate end = LocalDate.of(2023,3,31);

    List<PredictionResponseDTO> findCollectPrediction = researchService.getCollectPrediction(null, null,
        start, end);

    log.info("findCollectPrediction : " + findCollectPrediction);
    log.info("findCollectPrediction : " + findCollectPrediction.size());

  }
  // ---------- getCollectPrediction 메서드 테스트 끝 ---------

}