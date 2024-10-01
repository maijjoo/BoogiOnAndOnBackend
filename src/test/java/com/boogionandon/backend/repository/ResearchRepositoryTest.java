package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.ResearchSub;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.util.DistanceCalculator;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
    Long researcherId = 6L; // initData에서 만들어진 Worer id => 6L
    Worker researcher = (Worker) memberRepository.findById(researcherId)
        .orElseThrow(() -> new NoSuchElementException("Worker with id "+ researcherId +" not found"));
    // initData에서 만든 Worker의 id

    String beachName = "해운대해수욕장";
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
}