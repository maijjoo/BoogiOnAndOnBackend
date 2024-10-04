package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.ResearchSub;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
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
@Transactional
@Log4j2
class ResearchRepositoryTest {

  @Autowired
  private ResearchMainRepository researchMainRepository;
  @Autowired
  private ResearchSubRepository researchSubRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BeachRepository beachRepository;

  @Test
  @DisplayName("ResearchRepository들의 연결 확인")
  void testRepositoryConnection() {
    assertNotNull(researchMainRepository);
    assertNotNull(researchSubRepository);
    log.info("researchMainRepository : " + researchMainRepository.getClass().getName());
    log.info("researchSubRepository : " + researchSubRepository.getClass().getName());
  }

  @Test
  @DisplayName("research 추가 테스트")
  @Commit
  void testResearchInsert() {
    Long researcherId = 8L; // initData에서 만들어진 Worer id => 11L
    Worker researcher = (Worker) memberRepository.findById(researcherId)
        .orElseThrow(() -> new NoSuchElementException("Worker with id "+ researcherId +" not found"));
    // initData에서 만든 Worker의 id

    String beachName = "광안리해수욕장";
    Beach findBeach = beachRepository.findById(beachName)
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));

    // 리서치 메인 먼저 만들고 그다음 서브 넣을 예정
    ResearchMain researchMain = ResearchMain.builder()
        .researcher(researcher)
        .beach(findBeach)
        .expectedTrashAmount(150) // L
        .reportTime(LocalDateTime.now()) // 리포트 작성 시간
        // 이미지는 여기선 일단 패스
        .status(ReportStatus.ASSIGNMENT_NEEDED)
        .weather("맑음") // 오늘 날씨
        .specialNote("태풍") // 보고 올릴때 쓰레기가 특수 상황에 의해 발생한 것인지
        .totalBeachLength(0.0)
        .build();

    log.info("researchMain : " + researchMain);

    ResearchMain savedResearchMain = researchMainRepository.save(researchMain);

    Double totalBeachLength = 0.0;

    for (int i = 1; i <= 4; i++) {
      ResearchSub researchSub = ResearchSub.builder()
          .research(savedResearchMain)
          .beachNameWithIndex(findBeach.getBeachName() + i)
          // 반복으로 하니까 위, 경도 바꾸기가 쉽지않아 일단 동일하게 받음
          .startLatitude(35.15768265599188)
          .startLongitude(129.1572648115502)
          .endLatitude(35.15779193363473)
          .endLongitude(129.15770660944662)
          .mainTrashType(TrashType.대형_투기쓰레기류)
          // researchLength는 실제로 넣을 때는 위의 위,경도를 계산해서 넣기
          .researchLength(11.3)
          .build();

      totalBeachLength += DistanceCalculator.calculateDistance(
          researchSub.getStartLatitude(), researchSub.getStartLongitude(),
          researchSub.getEndLatitude(), researchSub.getEndLongitude()
      );

      ResearchSub savedResearchSub = researchSubRepository.save(researchSub);

      savedResearchMain.getResearchSubList().add(savedResearchSub);
    }

    savedResearchMain.setTotalResearch(totalBeachLength);
  }

  @Test
  @DisplayName("research 조회 테스트")
  void testResearchRead() {
    Long researchMainId = 1L;

    ResearchMain researchMain = researchMainRepository.findById(researchMainId)
        .orElseThrow(() -> new NoSuchElementException("ResearchMain with id "+ researchMainId +" not found"));

    // ToString으로 볼때는 무한 루프 빠질거 같은 건 빼놓았음
    log.info("researchMain : " + researchMain);
    log.info("researchSubs : " + researchMain.getResearchSubList());
  }

  // ------ findByStatusNeededAndSearch, findByStatusCompletedAndSearch 시작 ------
  @Test
  @DisplayName("findByStatusNeededAndSearch 조회 테스트 - 수퍼와 일반")
  void testFindByStatusNeededAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 7L;

    String beachSearch = "광안리";
    
    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();

    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("reportTime").descending() :
            Sort.by("reportTime").ascending()
    );

    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusNeededAndSearchForSuper(beachSearch, pageable);
//      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusNeededAndSearchForSuper("", pageable);

      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper);
      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper.getContent());
    } else {
      log.info("Admin 들어음");
      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusNeededAndSearchForRegular(beachSearch, pageable, adminId);
//      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusNeededAndSearchForRegular("", pageable, adminId);

      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular);
      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular.getContent());
    }


  }

  @Test
  @DisplayName("findByStatusCompletedAndSearch 조회 테스트")
  void testFindByStatusCompletedAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 7L;

    String beachSearch = "해운대";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("reportTime").descending() :
            Sort.by("reportTime").ascending()
    );

    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);


    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
//      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusCompletedAndSearchForSuper("", pageable);

      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper);
      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper.getContent());
    } else {
      log.info("Admin 들어음");
//      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusCompletedAndSearchForRegular("", pageable, adminId);

      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular);
      log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular.getContent());
    }
  }


  // ------ findByStatusNeededAndSearch 끝 ------
  // ------ findByIdWithOutSub, findListByMainId 시작 --------
  @Test
  @DisplayName("findByIdWithOutSub, findListByMainId 조회 ��스트")
  void testFindByIdWithOutSubAndFindListByMainId() {

    Long researchMainId = 1L;

    ResearchMain findMain = researchMainRepository.findByIdWithOutSub(researchMainId)
        .orElseThrow(() -> new EntityNotFoundException("해당 Research를 찾을 수 없습니다. : " + researchMainId));
    log.info("findMain : " + findMain.toString());

    List<ResearchSub> findSubList = researchSubRepository.findListByMainId(researchMainId);
    log.info("findSubList : " + findSubList);


  }
  // ------ findByIdWithOutSub, findListByMainId 끝 --------
}