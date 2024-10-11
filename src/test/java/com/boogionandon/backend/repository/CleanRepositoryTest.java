package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Image;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;
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

    Random random = new Random();

    Long cleanerId = 10L; // initData에서 만들어진 Worer id => 6L
    Worker findCleaner = (Worker) memberRepository.findById(cleanerId)
        .orElseThrow(() -> new NoSuchElementException("Worker with id "+ cleanerId +" not found"));
    // initData에서 만든 Worker의 id

    Admin managedAdmin = (Admin) memberRepository.findById(findCleaner.getManagerId())
        .orElseThrow(() -> new NoSuchElementException("Admin with id "+ findCleaner.getManagerId() +" not found"));

    List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();

    Beach randomBeach = beachRepository.findById(assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(random.nextInt(assignmentAreaList.size()))));

    int beforeRandomNumber = random.nextInt(13) + 3;
    int afterRandomNumber = random.nextInt(13) + 3;
    List<Image> images = new ArrayList<>();
    for (int i=0; i < beforeRandomNumber; i++) {
      Image image = Image.builder()
          .fileName("B_20241006005731_test.jpeg")
          .ord(i)
          .build();
      images.add(image);
    }
    for (int i=0; i < afterRandomNumber; i++) {
      Image image = Image.builder()
          .fileName("A_20241006005731_test.jpeg")
          .ord(i)
          .build();
      images.add(image);
    }

    Double startLatitude = 35.15768265599188;
    Double startLongitude = 129.15726481155502;
    Double endLatitude = 35.15779193363473;
    Double endLongitude = 129.15770660944662;

    Double beachLength = DistanceCalculator.calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);

    Clean clean = Clean.builder()
        .cleaner(findCleaner)
        .beach(randomBeach)
        .realTrashAmount(3) // 50L쓰레기 봉투를 기준으로 갯수로 계산 예정
        .cleanDateTime(java.time.LocalDateTime.now().minusYears(2))
        .startLatitude(startLatitude)
        .startLongitude(startLongitude)
        .endLatitude(endLatitude)
        .endLongitude(endLongitude)
        .beachLength(beachLength)
        .mainTrashType(TrashType.valueOf("폐어구류"))
        .images(images)
        .build();

    log.info("clean : " + clean.toString());

    cleanRepository.save(clean);
  }

  @Test
  @DisplayName("clean 추가 100개 테스트 - 이미지 제외")
  @Commit
  void testCleanInsert100() {
    List<Worker> cleaner = memberRepository.findAll().stream()
        .filter(member -> member instanceof Worker)
        .map(member -> (Worker) member)
        .collect(Collectors.toList());

    Random random = new Random();

    for (int i = 0; i < 100; i++) {
      Worker randomCleaner = cleaner.get(random.nextInt(cleaner.size()));

      Admin managedAdmin = (Admin) memberRepository.findById(randomCleaner.getManagerId())
          .orElseThrow(() -> new NoSuchElementException("Admin with id "+ randomCleaner.getManagerId() +" not found"));

      List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();

      Beach randomBeach = beachRepository.findById(assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
          .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(random.nextInt(assignmentAreaList.size()))));


      // 지정된 범위 내에서 임의의 날짜를 생성합니다.
      LocalDate startDate = LocalDate.of(2022, 2, 1);
      LocalDate endDate = LocalDate.now();
      long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
      LocalDate randomDate = startDate.plusDays(random.nextInt((int) daysBetween + 1));

      // 필요한 경우 월을 조정하세요.
      if (randomDate.getMonthValue() == 12 || randomDate.getMonthValue() == 1) {
        randomDate = randomDate.withMonth(random.nextInt(2, 12)); //2월부터 11월까지
      }

      LocalDateTime randomCleanDateTime = LocalDateTime.of(
          randomDate,
          LocalTime.of(random.nextInt(24), random.nextInt(60))
      );

      int beforeRandomNumber = random.nextInt(13) + 3;
      int afterRandomNumber = random.nextInt(13) + 3;
      List<Image> images = new ArrayList<>();
      for (int j=0; j < beforeRandomNumber; j++) {
        Image image = Image.builder()
            .fileName("B_20241006005731_test.jpeg")
            .ord(i)
            .build();
        images.add(image);
      }
      for (int j=0; j < afterRandomNumber; j++) {
        Image image = Image.builder()
            .fileName("A_20241006005731_test.jpeg")
            .ord(i)
            .build();
        images.add(image);
      }
      double randomOffset = (random.nextDouble() - 0.5) * 0.002; // 대략 100-200 meters

      double startLat = randomBeach.getLatitude() + randomOffset;
      double startLon = randomBeach.getLongitude() + randomOffset;
      double endLat = startLat + (random.nextDouble() - 0.5) * 0.0002; // 대략 10-20 meters
      double endLon = startLon + (random.nextDouble() - 0.5) * 0.0002;

      double beachLength = DistanceCalculator.calculateDistance(
          startLat, startLon, endLat, endLon);


      Clean clean = Clean.builder()
          .cleaner(randomCleaner)
          .beach(randomBeach)
          .realTrashAmount(random.nextInt(10) + 1)
          .cleanDateTime(randomCleanDateTime)
          .startLatitude(startLat)
          .startLongitude(startLon)
          .endLatitude(endLat)
          .endLongitude(endLon)
          .beachLength(beachLength)
          .images(images)
          .mainTrashType(TrashType.values()[random.nextInt(TrashType.values().length)])
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
    Integer year = 2022;

    List<Clean> findDateList = cleanRepository.findByDateCriteria(year, null, null, null);

    log.info("findDateList.size : " + findDateList.size());
    log.info("findDateList : " + findDateList);
  }

  @Test
  @DisplayName("쓰레기 분포도 보여주는 메서드 - 년/월 ")
  void testShowTrashDistributionWithYearAndMonth() {
    Integer year = 2023;
    Integer month = 5;

    List<Clean> findDateList = cleanRepository.findByDateCriteria(year, month, null, null);

    log.info("findDateList.size : " + findDateList.size());
    log.info("findDateList : " + findDateList);

  }

  @Test
  @DisplayName("쓰레기 분포도 보여주는 메서드 - 시작 ~ 끝 ")
  void testShowTrashDistributionbetweenStartAndEnd() {
    LocalDate start = LocalDate.of(2023, 5, 1);
    LocalDate end = LocalDate.of(2023, 6, 30);

    List<Clean> findDateList = cleanRepository.findByDateCriteria(null, null, start, end);

    log.info("findDateList.size : " + findDateList.size());
    log.info("findDateList : " + findDateList);
  }

  // getBasicStatistics 메서드 테스트
  @Test
  @DisplayName("기초 통계 보여주는 메서드 - lastYear - 4 ~ lastYear (연도별)")
  void testShowGetBasicStatisticsWith5YearsAgoToLastYearAndBeachName() {
    String tapCondition = "연도별";
    Integer year = 2024;
    String beachName = "광안리해수욕장";

    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, null, beachName);
//    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, null, null);

    log.info("basicStatistics: " + basicStatistics);
    log.info("basicStatistics.size : " + basicStatistics.size());
  }

  @Test
  @DisplayName("기초 통계 보여주는 메서드 - 해당 년도의 월별 데이터 (월별)")
  void testShowGetBasicStatisticsWithMonthlyOfYearAndBeachName() {

    String tapCondition = "월별";
    Integer year = 2023;
    String beachName = "광안리";

    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, null, beachName);

    log.info("basicStatistics: " + basicStatistics);
    log.info("basicStatistics.size : " + basicStatistics.size());

  }

  @Test
  @DisplayName("기초 통계 보여주는 메서드 - 해당 년도의 월의 1 ~ 31일(마지막날) 까지 (일별) ")
  void testShowGetBasicStatisticsWithDaysInMonthInYearAndBeachName() {

    String tapCondition = "일별";
    Integer year = 2024;
    Integer month = 10;
    String beachName = "광안리해수욕장";

    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, month, beachName);
//    List<Clean> basicStatistics = cleanRepository.getBasicStatistics(tapCondition, year, month, null);

    log.info("basicStatistics: " + basicStatistics);
    log.info("basicStatistics.size : " + basicStatistics.size());
  }

  // ------ findByStatusNeededAndSearch, findByStatusCompletedAndSearch 시작 ------
  @Test
  @DisplayName("findByStatusNeededAndSearch 조회 테스트 - 수퍼와 일반")
  void testFindByStatusNeededAndSearch() {

    // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
    // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
    Long adminId = 5L;

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
    Long adminId = 5L;

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
    Long cleanId = 12L; // initData에서 만들어진 Clean id => 1L

    Clean findClean = cleanRepository.findByIdWithImage(cleanId)
       .orElseThrow(() -> new EntityNotFoundException("Clean with id "+ cleanId +" not found"));

    log.info("clean : " + findClean.toString());
    log.info("clean images : " + findClean.getImages());
  }

  // ------ findByIdWithImage 끝 ------
}