package com.boogionandon.backend.config;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.repository.AdminRepository;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class InitialDataConfig {

  private final MemberRepository memberRepository;
  private final AdminRepository adminRepository;
  private final WorkerRepository workerRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  @Transactional
  public void initMembers() {
    if (memberRepository.count() == 0) {
      for( int i=1; i<=4; i++ ) {
        Admin admin = Admin.builder()
            .username("S_Busan" + i)
            .password(passwordEncoder.encode("0000"))
            .email("superadmin"+ i + "@ocean.net")
            .name("Super Admin" + i)
            .phone("010-1111-111"+i)
            .address("부산 연제구")
            .addressDetail("중앙대로 1001 부산광역시청")
            .workPlace("부산 시청")
            .department("해양농수산국") // 일단 할거 같은 곳
            .position("공무원") // 어떤 직급이 할지 모르겠음
            .assignmentArea("부산") // 수퍼 관리자라 부산 전체로 잡음
            .contact("051-1111-111"+i)
            .delFlag(false)
            .build();

        // managerId는 최상위 이기 때문에 null
        admin.getMemberRoleList().add(MemberType.SUPER_ADMIN);
        admin.getMemberRoleList().add(MemberType.ADMIN);
        adminRepository.save(admin);
        log.info("Super admin created");
      }

    } else {
      log.info("Super admin already exists");
    }
    // TODO : Admin이랑 Worker를 test용으로 만들까? 말까?

    if (memberRepository.count() == 4) {
      Admin admin = Admin.builder()
          .username("A_testAdmin")
          .password(passwordEncoder.encode("0000"))
          .email("test@admin.com")
          .name("김재원")
          .phone("010-9999-9999")
          .address("부산 광역시 수영구")
          .addressDetail("수영1동 100번지")
          .managerId(1L) // Super Admin1
          .workPlace("수영 구청")
          .department("해양수산")
          .position("공무원") // 직급체계 잘 모름
          .assignmentArea("해운대 해수욕장")
          .contact("051-9999-9999")
          .build();

      admin.getMemberRoleList().add(MemberType.ADMIN);
      adminRepository.save(admin);
      log.info("Admin created");
    } else {
      log.info("Amin already exists");
    }

    if (memberRepository.count() == 5) {
      Worker worker = Worker.builder()
          .username("W_testWorker")
          .password(passwordEncoder.encode("0000"))
          .email("test@worker.com")
          .name("이석현")
          .phone("010-8888-8888")
          .address("부산 광역시 수영구")
          .addressDetail("수영1동 101번지")
          .managerId(6L) // 위의 테스트에서 만들어진 Admin
          .contact("010-8888-8888")
          .workGroup("해운대 구청") // 이게 들어가는게 맞나?
          .workAddress("부산 광역시 해운대구")
          .workAddressDetail("중동2로 11 해운대구청")
          .build();

      // 차량 없다고 가정해서 vehicleCapacity은 null

      worker.getMemberRoleList().add(MemberType.WORKER);
      log.info("Worker : " + worker);
      workerRepository.save(worker);
    }else {
      log.info("Worker already exists");
    }
  }

}
