package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Log4j2
class PickUpRepositoryTest {

    @Autowired
    private PickUpRepository pickUpRepository;
    @Autowired
    private MemberRepository memberRepository;

    // ---------- PickUp 추가 테스트 시작-----------

    @Test
    @DisplayName("PickUp 추가 테스트")
    @Commit
    void testPickUpInsert() {
        String userName = "W_testWorker";
        Worker findWorker = (Worker) memberRepository.findByUsernameWithDetails(userName)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + userName));


        String pickUpPlace = "해운대 경찰서 앞"; // 집하지명 임의 설정

        PickUp pickup = PickUp.builder()
                .submitter(findWorker)
                .pickUpPlace(pickUpPlace)
                .latitude(129.1572648115502)
                .longitude(35.15768265599188)
                .mainTrashType(TrashType.대형_투기쓰레기류)
                .submitDateTime(LocalDateTime.now())
                .actualCollectedVolume(150.0)
                .build();

        // 3. PickUp 엔티티 리포지토리에 저장
        PickUp savedPickup = pickUpRepository.save(pickup);

        // 4. 결과 검증
        assertNotNull(savedPickup);
        assertNotNull(savedPickup.getId());
        log.info("집하지 등록이 성공적으로 저장되었습니다. ID: {}", savedPickup.getId());
    }

    @Test
    @DisplayName("랜덤 PickUp random개 추가 테스트")
    @Commit
    void testRandomPickUpInsert() {
        Random random = new Random();
        // workerUsernames 에는 W_testWorker, W_testWorker1, W_testWorker2, W_testWorker3
        // 4개가 initData에 의해 실행시 만들어짐
        String workerUsernames = "W_testWorker3";
        List<String> pickUpPlaces = Arrays.asList(
            "해운대 해수욕장 입구",
            "해운대 미포철길",
            "해운대 달맞이고개",
            "해운대 청사포",
            "해운대 동백섬",
            "해운대 해운대역 앞",
            "해운대 센텀시티",
            "해운대 영화의전당",
            "해운대 마린시티",
            "해운대 아쿠아리움",
            "해운대 APEC 누리마루",
            "해운대 오렌지족 거리",
            "해운대 해운대시장",
            "해운대 구남로",
            "해운대 해운대해변로",
            "해운대 송정해수욕장",
            "해운대 수영강 산책로",
            "해운대 반송동 습지",
            "해운대 벡스코",
            "해운대 우동 메가마트 앞"
        );

        List<PickUp> savedPickups = new ArrayList<>();

        int max = 10;
        int min = 1;
        int oneToTen = random.nextInt(max - min + 1) + min;

        for (int i = 0; i < oneToTen ; i++) {

            Worker worker = (Worker) memberRepository.findByUsernameWithDetails(workerUsernames)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다: " + workerUsernames));

            String randomPlace = pickUpPlaces.get(random.nextInt(pickUpPlaces.size()));

            PickUp pickup = PickUp.builder()
                .submitter(worker)
                .pickUpPlace(randomPlace)
                .latitude(getRandomLatitude())
                .longitude(getRandomLongitude())
                .mainTrashType(getRandomTrashType())
                .submitDateTime(LocalDateTime.now().minusHours(random.nextInt(24))) // 최근 24시간 내의 랜덤 시간
                .actualCollectedVolume(50.0 + random.nextDouble() * 200.0) // 50.0에서 250.0 사이의 랜덤 값
                .build();

            PickUp savedPickup = pickUpRepository.save(pickup);
            savedPickups.add(savedPickup);

            assertNotNull(savedPickup);
            assertNotNull(savedPickup.getId());
            log.info("집하지 등록이 성공적으로 저장되었습니다. ID: {}, 장소: {}", savedPickup.getId(), savedPickup.getPickUpPlace());
        }
    }

    private double getRandomLatitude() {
        // 부산 지역의 대략적인 위도 범위
        return 35.0 + (new Random().nextDouble() * 0.3);
    }

    private double getRandomLongitude() {
        // 부산 지역의 대략적인 경도 범위
        return 128.8 + (new Random().nextDouble() * 0.5);
    }

    private TrashType getRandomTrashType() {
        TrashType[] types = TrashType.values();
        return types[new Random().nextInt(types.length)];
    }
    // ---------- PickUp 추가 테스트 끝-----------

    // ---------- findPickUpWithAdmin 조회 테스트 시작-----------

    @Test
    @DisplayName("findPickUpWithAdminAndImages 조회 테스트")
    void testFindPickUpWithAdminAndImages() {

        // adminId는 기본적으로 5L, 6L, 7L 이 initData에 의해 자동으로 만들어짐
        Long adminId = 5L; // initData에서 자동으로 만든 admin 계정

        List<PickUp> findPickUpWithAdminAndImages = pickUpRepository.findPickUpWithAdminAndImages(adminId);

        log.info("findPickUpWithAdmin.size : " + findPickUpWithAdminAndImages.size());
        log.info("findPickUpWithAdmin : " + findPickUpWithAdminAndImages.toString());

    }

    // ---------- findPickUpWithAdmin 조회 테스트 끝-----------
    // ---------- findByStatusCompletedAndSearch 조회 테스트 시작-----------

    @Test
    @DisplayName("findByStatusCompletedAndSearch 조회 테스트")
    void testFindByStatusCompletedAndSearch() {

        // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
        // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
        Long adminId = 1L;

        String search = "해운대";

        // 기본으로 사용
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
            .build();


        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
            pageRequestDTO.getSort().equals("desc") ?
                Sort.by("submitDateTime").descending() :
                Sort.by("submitDateTime").ascending()
        );

        Member admin = memberRepository.findById(adminId)
            .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

        log.info("admin role : " + admin.getMemberRoleList().toString());

        // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
        boolean isContainSuper = admin.getMemberRoleList().stream()
            .anyMatch(role -> role == MemberType.SUPER_ADMIN);

        if (isContainSuper) {
            log.info("SuperAdmin 들어음");
//      Page<PickUp> byStatusCompletedAndSearchForSuper = pickUpRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
            Page<PickUp> byStatusCompletedAndSearchForSuper = pickUpRepository.findByStatusCompletedAndSearchForSuper("", pageable);

            log.info("byStatusCompletedAndSearchForSuper : " + byStatusCompletedAndSearchForSuper);
            log.info("byStatusCompletedAndSearchForSuper : " + byStatusCompletedAndSearchForSuper.getContent());
        } else {
            log.info("Admin 들어음");
//      Page<PickUp> byStatusCompletedAndSearchForRegular = pickUpRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
            Page<PickUp> byStatusCompletedAndSearchForRegular = pickUpRepository.findByStatusCompletedAndSearchForRegular("", pageable, adminId);

            log.info("byStatusCompletedAndSearchForRegular : " + byStatusCompletedAndSearchForRegular);
            log.info("byStatusCompletedAndSearchForRegular : " + byStatusCompletedAndSearchForRegular.getContent());
        }
    }


        // ---------- findByStatusCompletedAndSearch 조회 테스트 끝-----------
}