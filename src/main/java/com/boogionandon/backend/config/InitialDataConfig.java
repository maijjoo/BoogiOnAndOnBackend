package com.boogionandon.backend.config;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.repository.AdminRepository;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
  private final BeachRepository beachRepository;

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

      for (int i = 1 ; i <= 2; i++) {
        Admin adminPlus = Admin.builder()
            .username("A_testAdmin" + i)
            .password(passwordEncoder.encode("0000"))
            .email("test" + i + "@admin.com")
            .name("송지현")
            .phone("010-9999-999" + i)
            .address("부산 광역시 수영구")
            .addressDetail("수영2동 200번지")
            .managerId(1L) // Super Admin1
            .workPlace("수영 구청")
            .department("해양수산")
            .position("공무원") // 직급체계 잘 모름
            .assignmentArea("해운대 해수욕장")
            .contact("051-9999-999" + i)
            .build();

        adminPlus.getMemberRoleList().add(MemberType.ADMIN);
        adminRepository.save(adminPlus);
        log.info("adminPlus : " + adminPlus);

      }
    } else {
      log.info("Amin already exists");
    }

    if (memberRepository.count() == 7) {
      Worker worker = Worker.builder()
          .username("W_testWorker")
          .password(passwordEncoder.encode("0000"))
          .email("test@worker.com")
          .name("이석현")
          .phone("010-8888-8888")
          .address("부산 광역시 수영구")
          .addressDetail("수영3동 301번지")
          .managerId(5L) // 위의 테스트에서 만들어진 Admin
          .contact("010-8888-8888")
          .workGroup("해운대 구청") // 이게 들어가는게 맞나?
          .workAddress("부산 광역시 해운대구")
          .workAddressDetail("중동2로 11 해운대구청")
          .startDate(LocalDate.now()) // 실제는 화면에서 선택
          .endDate(LocalDate.now().plusMonths(6)) // 실제는 화면에서 선택
          .build();

      // 차량 없다고 가정해서 vehicleCapacity은 null

      worker.getMemberRoleList().add(MemberType.WORKER);
      log.info("Worker : " + worker);
      workerRepository.save(worker);

      for (int i = 1; i <= 3; i++) {
        Worker workerPlus = Worker.builder()
            .username("W_testWorker"+i)
            .password(passwordEncoder.encode("0000"))
            .email("test" + i + "@worker.com")
            .name("라주엽")
            .phone("010-8888-888" + i)
            .address("부산 광역시 수영구")
            .addressDetail("수영4동 401번지")
            .managerId(6L) // 위의 테스트에서 만들어진 Admin
            .contact("010-8888-888"+i)
            .workGroup("해운대 구청") // 이게 들어가는게 맞나?
            .workAddress("부산 광역시 해운대구")
            .workAddressDetail("중동2로 11 해운대구청")
            .startDate(LocalDate.now()) // 실제는 화면에서 선택
            .endDate(LocalDate.now().plusMonths(6)) // 실제는 화면에서 선택
            .vehicleCapacity(i)
            .build();

        workerPlus.getMemberRoleList().add(MemberType.WORKER);
        log.info("workerPlus : " + workerPlus);
        workerRepository.save(workerPlus);
      }
    }else {
      log.info("Worker already exists");
    }
  }

  @PostConstruct
  @Transactional
  public void initBeaches() {
    if (beachRepository.count() == 0) {
      List<Beach> beaches = Arrays.asList(
          // 공식 해수욕장
          Beach.builder().beachName("해운대해수욕장").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1586).longitude(129.1603).build(),
          Beach.builder().beachName("광안리해수욕장").si("부산광역시").guGun("수영구").dongEub("광안동").workplace("수영구").latitude(35.1532).longitude(129.1182).build(),
          Beach.builder().beachName("송정해수욕장").si("부산광역시").guGun("해운대구").dongEub("송정동").workplace("해운대구").latitude(35.1785).longitude(129.1994).build(),
          Beach.builder().beachName("다대포해수욕장").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
          Beach.builder().beachName("송도해수욕장").si("부산광역시").guGun("서구").dongEub("암남동").workplace("서구").latitude(35.0764).longitude(129.0243).build(),
          Beach.builder().beachName("일광해수욕장").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
          Beach.builder().beachName("임랑해수욕장").si("부산광역시").guGun("기장군").dongEub("장안읍").workplace("기장군").latitude(35.3184).longitude(129.2646).build(),

          // 기타 해변 및 해안가
          Beach.builder().beachName("감지해변").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2741).longitude(129.2437).build(),
          Beach.builder().beachName("국립부산과학관 해변").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
          Beach.builder().beachName("다선해변").si("부산광역시").guGun("영도구").dongEub("청학동").workplace("영도구").latitude(35.0891).longitude(129.0746).build(),
          Beach.builder().beachName("몰운대").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0411).longitude(128.9663).build(),
          Beach.builder().beachName("미포").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1716).build(),
          Beach.builder().beachName("송림해변").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2678).longitude(129.2384).build(),
          Beach.builder().beachName("암남공원").si("부산광역시").guGun("서구").dongEub("암남동").workplace("서구").latitude(35.0679).longitude(129.0193).build(),
          Beach.builder().beachName("오륙도").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1016).longitude(129.1225).build(),
          Beach.builder().beachName("이기대").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1166).longitude(129.1225).build(),
          Beach.builder().beachName("일광해안").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
          Beach.builder().beachName("장안사계해변").si("부산광역시").guGun("기장군").dongEub("장안읍").workplace("기장군").latitude(35.3184).longitude(129.2646).build(),
          Beach.builder().beachName("죽성성게마을").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
          Beach.builder().beachName("청사포").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1908).build(),
          Beach.builder().beachName("태종대").si("부산광역시").guGun("영도구").dongEub("동삼동").workplace("영도구").latitude(35.0532).longitude(129.0854).build(),

          // 포구 및 항구
          Beach.builder().beachName("감천항").si("부산광역시").guGun("사하구").dongEub("감천동").workplace("사하구").latitude(35.0813).longitude(129.0022).build(),
          Beach.builder().beachName("기장항").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
          Beach.builder().beachName("남포동").si("부산광역시").guGun("중구").dongEub("남포동").workplace("중구").latitude(35.0987).longitude(129.0317).build(),
          Beach.builder().beachName("다대포항").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
          Beach.builder().beachName("부산항").si("부산광역시").guGun("영도구").dongEub("남항동").workplace("영도구").latitude(35.0967).longitude(129.0358).build(),
          Beach.builder().beachName("영도").si("부산광역시").guGun("영도구").dongEub("남항동").workplace("영도구").latitude(35.0910).longitude(129.0444).build(),
          Beach.builder().beachName("용호부두").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1166).longitude(129.1225).build(),
          Beach.builder().beachName("자갈치시장").si("부산광역시").guGun("중구").dongEub("남포동").workplace("중구").latitude(35.0967).longitude(129.0304).build(),
          Beach.builder().beachName("조도").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
          Beach.builder().beachName("충무동").si("부산광역시").guGun("중구").dongEub("충무동").workplace("중구").latitude(35.1015).longitude(129.0293).build(),

          // 추가된 항구 및 해안가
          Beach.builder().beachName("천성항").si("부산광역시").guGun("강서구").dongEub("천성동").workplace("강서구").latitude(35.0494).longitude(128.8147).build(),
          Beach.builder().beachName("명지항").si("부산광역시").guGun("강서구").dongEub("명지동").workplace("강서구").latitude(35.0761).longitude(128.9028).build(),
          Beach.builder().beachName("대변항").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.2235).longitude(129.2259).build(),
          Beach.builder().beachName("칠암항").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
          Beach.builder().beachName("미포항").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1716).build(),
          Beach.builder().beachName("민락수변공원").si("부산광역시").guGun("수영구").dongEub("민락동").workplace("수영구").latitude(35.1563).longitude(129.1284).build(),
          Beach.builder().beachName("송정항").si("부산광역시").guGun("해운대구").dongEub("송정동").workplace("해운대구").latitude(35.1785).longitude(129.1994).build(),
          Beach.builder().beachName("동백섬").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1531).longitude(129.1530).build(),
          Beach.builder().beachName("남천동 해안").si("부산광역시").guGun("수영구").dongEub("남천동").workplace("수영구").latitude(35.1403).longitude(129.1148).build(),
          Beach.builder().beachName("영도 깍천").si("부산광역시").guGun("영도구").dongEub("동삼동").workplace("영도구").latitude(35.0789).longitude(129.0876).build(),
          Beach.builder().beachName("아미산 전망대").si("부산광역시").guGun("사하구").dongEub("감천동").workplace("사하구").latitude(35.0900).longitude(129.0069).build(),
          Beach.builder().beachName("용두산공원").si("부산광역시").guGun("중구").dongEub("광복동").workplace("중구").latitude(35.1010).longitude(129.0325).build()
      );

      beachRepository.saveAll(beaches);
    }
  }
}
