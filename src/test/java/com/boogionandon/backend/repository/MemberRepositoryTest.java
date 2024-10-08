package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.service.BeachService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class MemberRepositoryTest {

  @Autowired private MemberRepository memberRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private BeachService beachService;

  @Test
  @DisplayName("MemberRepository 연결 확인")
  void repositoryConnectionTest() {
    Assertions.assertNotNull(memberRepository);

    log.info("memberRepository : " + memberRepository.getClass().getName());
  }

  @Test
  @DisplayName("Admin 회원 생성테스트")
  @Commit // 롤백 안되게 하기 위한
  void createAdminTest() {

    // siList : [부산광역시]
    List<String> siList = beachService.SortedSiList();
    String si = siList.get(0);

    // guGunSet : [강서구, 기장군, 남구, 사하구, 서구, 수영구, 영도구, 중구, 해운대구]
    // List<String> guGunList = beachService.SortedGuGunList(); // 여기서는 필요 없을듯

    // sortedSiGuGunMap : {부산광역시=[강서구, 기장군, 남구, 사하구, 서구, 수영구, 영도구, 중구, 해운대구]}
    Map<String, List<String>> guGunMap = beachService.sortedSiGuGunMap();

    // beachNameMap : {강서구=[명지항, 천성항],
    // 기장군=[감지해변, 국립부산과학관 해변, 기장항, 대변항, 송림해변, 일광해수욕장, 일광해안, 임랑해수욕장, 장안사계해변, 죽성성게마을, 칠암항],
    // 남구=[오륙도, 용호부두, 이기대],
    // 사하구=[감천항, 다대포항, 다대포해수욕장, 몰운대, 아미산 전망대, 조도],
    // 서구=[송도해수욕장, 암남공원], 수영구=[광안리해수욕장, 남천동 해안, 민락수변공원],
    // 영도구=[다선해변, 부산항, 영도, 영도 깍천, 태종대], 중구=[남포동, 용두산공원, 자갈치시장, 충무동],
    // 해운대구=[동백섬, 미포, 미포항, 송정항, 송정해수욕장, 청사포, 해운대해수욕장]}
    Map<String, List<String>> beachNameMap = beachService.SortedBeachNameMap();

    log.info("si : " + si);
    log.info("guGunMap : " + guGunMap.toString());
    log.info("beachNameMap : " + beachNameMap.toString());

    Admin admin = Admin.builder()
        .username("A_testAdmin9")
        .password(passwordEncoder.encode("0000"))
        .email("test99@admin.com")
        .name("김재원")
        .phone("010-1234-9678")
        .address("부산 광역시 수영구")
        .addressDetail("수영1동 100번지")
        .managerId(1L) // Super Admin1
        .workCity(si)
        .workPlace(guGunMap.get(si).get(0))
        .department("해양수산")
        .position("공무원") // 직급체계 잘 모름
        .assignmentAreaList(beachNameMap.get(guGunMap.get(si).get(0)))
        .contact("051-1934-5678")
        .build();

    admin.getMemberRoleList().add(MemberType.ADMIN);

    log.info("admin : ", admin);

    memberRepository.save(admin);
  }

  @Test
  @DisplayName("Admin 회원 조회 테스트")
  void adminReadTest() {

    String username = "A_testAdmin9";

    // 3중 원하는 방시 아무거나 써도 될듯

    Member savedMember = memberRepository.findByUsernameWithDetails(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    log.info("savedMember : " + savedMember.toString());
    log.info("savedMamber.username : " + savedMember.getUsername());

    Admin savedAdmin = (Admin) savedMember;

    log.info("savedAdmin : " + savedAdmin.toString());

    Admin admin = (Admin) memberRepository.findByUsernameWithDetails(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    log.info("admin : " + admin.toString());
  }

  @Test
  @DisplayName("Worker 회원 생성 테스트")
  @Commit // 롤백 안되게 하기 위한
  void createWorkerTest() {

    Worker worker = Worker.builder()
        .username("W_testWorker13")
        .password(passwordEncoder.encode("0000"))
        .email("test16@worker.com")
        .name("이석현")
        .phone("010-1111-2422")
        .address("부산 광역시 수영구")
        .addressDetail("수영1동 101번지")
        .managerId(5L) // 위의 테스트에서 만들어진 Admin
        .contact("010-1234-1234")
        .workGroup("해운대 구청") // 이게 들어가는게 맞나?
        .workAddress("부산 광역시 해운대구")
        .workAddressDetail("중동2로 11 해운대구청")
        .startDate(LocalDate.now()) // 실제는 화면에서 선택
        .endDate(LocalDate.now().plusMonths(6)) // 실제는 화면에서 선택
        .build();

    // 차량 없다고 가정해서 vehicleCapacity은 null

    worker.getMemberRoleList().add(MemberType.WORKER);

    log.info("Worker : " + worker);

    memberRepository.save(worker);
  }

  @Test
  @DisplayName("Worker 회원 조회 테스트")
  void workerReadTest() {
    String username = "W_testWorker13";

    // 3중 원하는 방시 아무거나 써도 될듯

    Member savedMember = memberRepository.findByUsernameWithDetails(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    log.info("savedMember : " + savedMember.toString());
    log.info("savedMamber.username : " + savedMember.getUsername());

    Worker savedWorker = (Worker) savedMember;

    log.info("savedWorker : " + savedWorker);

    Worker worker = (Worker) memberRepository.findByUsernameWithDetails(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    log.info("worker : " + worker.toString());
  }

  // ------------ 회원 조회 관련 메서드 시작 -----------------
  @Test
  @DisplayName("findAllByWorkerManagedAdminWithNameSearchForRegular 메서드 테스트")
  void testFindAllByWorkerManagedAdminWithNameSearchForRegularTest() {

    // 일반 admin의 id를 사용
    Long adminId = 5L; // initData에 의해 자동으로 들어가있는 테스트용 -> 5L,6L,7L

    String nameSearch = "";

    String tabCondition = "수거자"; // 전체, 조사/청소, 수거자

    // pageable 생성
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("createdDate").descending() :
            Sort.by("createdDate").ascending()
    );

    Page<Member> memberList = memberRepository.findAllByWorkerManagedAdminWithNameSearchForRegular(adminId, tabCondition, nameSearch, pageable);
    log.info("memberList : " + memberList);
  }

  @Test
  @DisplayName("findAllByWorkerManagedAdminWithNameSearchForSuper 메서드 테스트")
  void testFindAllByWorkerManagedAdminWithNameSearchForSuperTest() {

    // Super admin의 id를 사용
    Long adminId = 1L; // initData에 의해 자동으로 들어가있는 테스트용 -> 1L,2L,3L

    String nameSearch = "";

    String tabCondition = "관리자"; // 관리자, 조사/청소, 수거자

    // pageable 생성
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize(),
        pageRequestDTO.getSort().equals("desc") ?
            Sort.by("createdDate").descending() :
            Sort.by("createdDate").ascending()
    );

    Page<Member> memberList = memberRepository.findAllByWorkerManagedAdminWithNameSearchForSuper(adminId, tabCondition, nameSearch, pageable);
    log.info("memberList : " + memberList);

  }
  // ------------ 회원 조회 관련 메서드 끝 -----------------

  @Test
  @DisplayName("findWorkerWithAdminInfo Test")
  void testFindWorkerWithAdminInfo() {
    Long workerId = 5L; // initData에 의해 자동으로 들어가��는 ��스트용 -> 5L,6L,7L

    Optional<Object> workerWithAdminInfo = memberRepository.findByIdWithManager(workerId);

    if (workerWithAdminInfo.isPresent()) {
      log.info("workerWithAdminInfo : " + workerWithAdminInfo.get());
    } else {
      log.info("workerWithAdminInfo is not found.");
    }
  }

  @Test
  @DisplayName("findByIdWithManager Test")
  void testFindByIdWithManager() {
    Long workerId = 8L; // initData에 의해 자동저장된 8L, 9L, 10L, 11L

    Optional<Object> workerWithAdminInfo = memberRepository.findByIdWithManager(workerId);

    workerWithAdminInfo.ifPresent(result -> {
      if (result instanceof Object[]) {
        Object[] resultArray = (Object[]) result;
        if (resultArray.length >= 2) {
          Object memberInfo = resultArray[0];
          Object adminInfo = resultArray[1];

          log.info("Member Info: {}", memberInfo);
          log.info("Admin Info: {}", adminInfo);

          // Worker 정보 처리
          if (memberInfo instanceof Worker) {
            Worker worker = (Worker) memberInfo;
            log.info("Worker Name: {}", worker.getName());
            // 추가 Worker 필드 접근...
          } else if (memberInfo instanceof Admin) {
            Admin admin = (Admin) memberInfo;
            log.info("Admin Name: {}", admin.getName());
          }

          // Admin 정보 처리
          if (adminInfo instanceof Admin) {
            Admin admin = (Admin) adminInfo;
            log.info("Admin Name: {}", admin.getName());
            // 추가 Admin 필드 접근...
          }
        } else {
          log.info("Result array does not contain expected number of elements");
        }
      } else {
        log.info("Unexpected result type: {}", result.getClass().getName());
      }
    });

    if (workerWithAdminInfo.isEmpty()) {
      log.info("No result found for worker ID: {}", workerId);
    }
  }

}