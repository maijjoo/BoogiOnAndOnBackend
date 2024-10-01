package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.util.DistanceCalculator;
import java.util.NoSuchElementException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
class CleanRepositoryTest {

  @Autowired
  private CleanRepository cleanRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BeachRepository beachRepository;

  @Test
  @DisplayName("DI 확인")
  void testRepositoryConnection() {
    assertNotNull(cleanRepository);
    assertNotNull(memberRepository);
    log.info("cleanRepository : " + cleanRepository.getClass().getName());
    log.info("memberRepository : " + memberRepository.getClass().getName());
  }

  @Test
  @DisplayName("clean 추가 테스트")
  @Commit
  void testCleanInsert() {

    Long cleanerId = 6L; // initData에서 만들어진 Worer id => 6L
    Worker findCleaner = (Worker) memberRepository.findById(cleanerId)
        .orElseThrow(() -> new NoSuchElementException("Worker with id "+ cleanerId +" not found"));
    // initData에서 만든 Worker의 id

    String beachName = "해운대해수욕장";
    Beach findBeach = beachRepository.findById(beachName)
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));

    Double startLatitude = 35.15768265599188;
    Double startLongitude = 129.15726481155502;
    Double endLatitude = 35.15779193363473;
    Double endLongitude = 129.15770660944662;

    Double beachLength = DistanceCalculator.calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);

    Clean clean = Clean.builder()
        .cleaner(findCleaner)
        .beach(findBeach)
        .realTrashAmount(3) // 50L쓰레기 봉투를 기준으로 갯수로 계산 예정
        .cleanDateTime(java.time.LocalDateTime.now())
        .startLatitude(startLatitude)
        .startLongitude(startLongitude)
        .endLatitude(endLatitude)
        .endLongitude(endLongitude)
        .beachLength(beachLength)
        .mainTrashType(TrashType.valueOf("폐어구류"))
        // 이미지는 test에서 생략
        .build();

    log.info("clean : " + clean.toString());

    cleanRepository.save(clean);
  }

  @Test
  @DisplayName("clean 조회 테스트")
  void testCleanRead() {

    Long cleanId = 1L; // initData에서 만들어진 Clean id => 1L

    Clean findClean = cleanRepository.findById(cleanId)
       .orElseThrow(() -> new NoSuchElementException("Clean with id "+ cleanId +" not found"));

    // ToString으로 볼때는 무한 루프 빠질거 같은 건 빼놓았음
    log.info("clean : " + findClean.toString());

  }
}