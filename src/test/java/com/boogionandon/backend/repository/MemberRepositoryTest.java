package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import java.time.LocalDate;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    Admin admin = Admin.builder()
        .username("A_testAdmin")
        .password(passwordEncoder.encode("0000"))
        .email("test@admin.com")
        .name("김재원")
        .phone("010-1234-5678")
        .address("부산 광역시 수영구")
        .addressDetail("수영1동 100번지")
        .managerId(1L) // Super Admin1
        .workPlace("수영 구청")
        .department("해양수산")
        .position("공무원") // 직급체계 잘 모름
        .assignmentArea("해운대 해수욕장")
        .contact("051-1234-5678")
        .build();

    admin.getMemberRoleList().add(MemberType.ADMIN);

    log.info("admin : ", admin);

    memberRepository.save(admin);
  }

  @Test
  @DisplayName("Admin 회원 조회 테스트")
  void adminReadTest() {

    String username = "A_testAdmin";

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
        .username("W_testWorker")
        .password(passwordEncoder.encode("0000"))
        .email("test@worker.com")
        .name("이석현")
        .phone("010-1111-2222")
        .address("부산 광역시 수영구")
        .addressDetail("수영1동 101번지")
        .managerId(6L) // 위의 테스트에서 만들어진 Admin
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
    String username = "W_testWorker";

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


}