package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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

    Long cleanerId = 10L; // initData에서 만들어진 Worer id => 6L
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
  @DisplayName("clean 추가 100개 테스트 - 이미지 제외")
  @Commit
  void testCleanInsert100() {
    final List<String> BEACH_NAMES = List.of(
        "해운대해수욕장", "광안리해수욕장", "송정해수욕장", "다대포해수욕장", "송도해수욕장",
        "일광해수욕장", "임랑해수욕장", "감지해변", "국립부산과학관 해변", "다선해변",
        "몰운대", "미포", "송림해변", "암남공원", "오륙도", "이기대", "일광해안",
        "장안사계해변", "죽성성게마을", "청사포", "태종대"
    );

    final List<TrashType> TRASH_TYPES = List.of(
        TrashType.폐어구류, TrashType.부표류, TrashType.생활쓰레기류,
        TrashType.대형_투기쓰레기류, TrashType.초목류
    );

    Random random = new Random();

    Long cleanerId = 9L; // initData에서 만들어진 Worer id => 8L, 9L, 10L, 11L
    Worker findCleaner = (Worker) memberRepository.findById(cleanerId)
        .orElseThrow(() -> new NoSuchElementException("Worker with id "+ cleanerId +" not found"));
    // initData에서 만든 Worker의 id

    for (int i = 0; i < 100; i++) {
      String beachName = BEACH_NAMES.get(random.nextInt(BEACH_NAMES.size()));

      Beach findBeach = beachRepository.findById(beachName)
          .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));

      double baseLatitude = findBeach.getLatitude();
      double baseLongitude = findBeach.getLongitude();

      double startLatitude = baseLatitude + (random.nextDouble() - 0.5) * 0.002;
      double startLongitude = baseLongitude + (random.nextDouble() - 0.5) * 0.002;
      double endLatitude = startLatitude + (random.nextDouble() - 0.5) * 0.001;
      double endLongitude = startLongitude + (random.nextDouble() - 0.5) * 0.001;

      double beachLength = DistanceCalculator.calculateDistance(
          startLatitude, startLongitude, endLatitude, endLongitude);

      LocalDateTime cleanDateTime = LocalDateTime.of(
          ThreadLocalRandom.current().nextInt(2020, 2025),
          ThreadLocalRandom.current().nextInt(1, 13),
          ThreadLocalRandom.current().nextInt(1, 29),
          ThreadLocalRandom.current().nextInt(0, 24),
          ThreadLocalRandom.current().nextInt(0, 60)
      );

      Clean clean = Clean.builder()
          .cleaner(findCleaner)
          .beach(findBeach)
          .realTrashAmount(random.nextInt(10) + 1)
          .cleanDateTime(cleanDateTime)
          .startLatitude(startLatitude)
          .startLongitude(startLongitude)
          .endLatitude(endLatitude)
          .endLongitude(endLongitude)
          .beachLength(beachLength)
          .mainTrashType(TRASH_TYPES.get(random.nextInt(TRASH_TYPES.size())))
          .build();

      log.info("clean : " + clean.toString());

      cleanRepository.save(clean);
    }

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

  // findByDateCriteria 메서드 테스트
  @Test
  @DisplayName("쓰레기 분포도 보여주는 메서드 - 년 ")
  void testShowTrashDistributionWithYear() {
    Integer year = 2023;

    List<Clean> findDateList = cleanRepository.findByDateCriteria(year, null, null, null);

    log.info("findDateList : " + findDateList);
  }

  @Test
  @DisplayName("쓰레기 분포도 보여주는 메서드 - 년/월 ")
  void testShowTrashDistributionWithYearAndMonth() {
    Integer year = 2023;
    Integer month = 6;

    List<Clean> findDateList = cleanRepository.findByDateCriteria(year, month, null, null);

    log.info("findDateList : " + findDateList);

  }

  @Test
  @DisplayName("쓰레기 분포도 보여주는 메서드 - 시작 ~ 끝 ")
  void testShowTrashDistributionbetweenStartAndEnd() {
    LocalDate start = LocalDate.of(2023, 6, 1);
    LocalDate end = LocalDate.of(2023, 6, 30);

    List<Clean> findDateList = cleanRepository.findByDateCriteria(null, null, start, end);

    log.info("findDateList : " + findDateList);
  }

  // getBasicStatistics 메서드 테스트
  @Test
  @DisplayName("기초 통계 보여주는 메서드 - lastYear - 4 ~ lastYear (연도별)")
  void testShowGetBasicStatisticsWith5YearsAgoToLastYearAndBeachName() {
    String tapCondition = "연도별";
    Integer year = 2023;
    String beachName = "해운대해수욕장";

    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, null, beachName);

    log.info("basicStatistics: " + basicStatistics);
  }

  @Test
  @DisplayName("기초 통계 보여주는 메서드 - 해당 년도의 월별 데이터 (월별)")
  void testShowGetBasicStatisticsWithMonthlyOfYearAndBeachName() {

    String tapCondition = "월별";
    Integer year = 2021;
    String beachName = "해운대해수욕장";

    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, null, beachName);

    log.info("basicStatistics: " + basicStatistics);

  }

  @Test
  @DisplayName("기초 통계 보여주는 메서드 - 해당 년도의 월의 1 ~ 31일(마지막날) 까지 (일별) ")
  void testShowGetBasicStatisticsWithDaysInMonthInYearAndBeachName() {

    String tapCondition = "일별";
    Integer year = 2021;
    Integer month = 11;
    String beachName = "국립";

//    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, month, beachName);
    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, month, null);

    log.info("basicStatistics: " + basicStatistics);
  }

  // ------ findByStatusNeededAndSearch, findByStatusCompletedAndSearch 시작 ------
  @Test
  @DisplayName("findByStatusNeededAndSearch 조회 테스트 - 수퍼와 일반")
  void testFindByStatusNeededAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 6L;

    String beachSearch = "광안리";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("cleanDateTime").descending() :
            Sort.by("cleanDateTime").ascending()
    );

    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
//      Page<Clean> byStatusNeededAndSearchForSuper = cleanRepository.findByStatusNeededAndSearchForSuper(beachSearch, pageable);
      Page<Clean> byStatusNeededAndSearchForSuper = cleanRepository.findByStatusNeededAndSearchForSuper("", pageable);

      log.info("byStatusNeededAndSearchForSuper : " + byStatusNeededAndSearchForSuper);
      log.info("byStatusNeededAndSearchForSuper : " + byStatusNeededAndSearchForSuper.getContent());
    } else {
      log.info("Admin 들어음");
//      Page<Clean> byStatusNeededAndSearchForRegular = cleanRepository.findByStatusNeededAndSearchForRegular(beachSearch, pageable, adminId);
      Page<Clean> byStatusNeededAndSearchForRegular = cleanRepository.findByStatusNeededAndSearchForRegular("", pageable, adminId);

      log.info("byStatusNeededAndSearchForRegular : " + byStatusNeededAndSearchForRegular);
      log.info("byStatusNeededAndSearchForRegular : " + byStatusNeededAndSearchForRegular.getContent());
    }
  }

  @Test
  @DisplayName("findByStatusCompletedAndSearch 조회 테스트")
  void testFindByStatusCompletedAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 6L;

    String beachSearch = "해운대";

    // 기본으로 사용
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
        .build();


    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("cleanDateTime").descending() :
            Sort.by("cleanDateTime").ascending()
    );

    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

// size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
//      Page<Clean> byStatusCompletedAndSearchForSuper = cleanRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
      Page<Clean> byStatusCompletedAndSearchForSuper = cleanRepository.findByStatusCompletedAndSearchForSuper("", pageable);

      log.info("byStatusCompletedAndSearchForSuper : " + byStatusCompletedAndSearchForSuper);
      log.info("byStatusCompletedAndSearchForSuper : " + byStatusCompletedAndSearchForSuper.getContent());
    } else {
      log.info("Admin 들어음");
//      Page<Clean> byStatusCompletedAndSearchForRegular = cleanRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
      Page<Clean> byStatusCompletedAndSearchForRegular = cleanRepository.findByStatusCompletedAndSearchForRegular("", pageable, adminId);

      log.info("byStatusCompletedAndSearchForRegular : " + byStatusCompletedAndSearchForRegular);
      log.info("byStatusCompletedAndSearchForRegular : " + byStatusCompletedAndSearchForRegular.getContent());
    }
  }
  // ------ findByStatusNeededAndSearch, findByStatusCompletedAndSearch 끝 ------
  // ------ findByIdWithImage 시작 ------
  @Test
  @DisplayName("findByIdWithImage 메서드 테스트")
  void testFindByIdWithImage() {
    Long cleanId = 103L; // initData에서 만들어진 Clean id => 1L

    Clean findClean = cleanRepository.findByIdWithImage(cleanId)
       .orElseThrow(() -> new EntityNotFoundException("Clean with id "+ cleanId +" not found"));

    log.info("clean : " + findClean.toString());
    log.info("clean images : " + findClean.getImages());
  }

  // ------ findByIdWithImage 끝 ------
}