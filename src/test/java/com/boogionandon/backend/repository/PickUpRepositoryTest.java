package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Image;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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
class PickUpRepositoryTest {

    @Autowired
    private PickUpRepository pickUpRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BeachRepository beachRepository;

    // ---------- PickUp 추가 테스트 시작-----------

    @Test
    @DisplayName("PickUp 추가 테스트")
    @Commit
    void testPickUpInsert() {
        String userName = "W_testWorker";
        Worker findWorker = (Worker) memberRepository.findByUsernameWithDetails(userName)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + userName));

        Random random = new Random();

        String pickUpPlace = "경찰서 앞"; // 집하지명 임의 설정

        int randomNumber = random.nextInt(1, 3);
        List<Image> images = new ArrayList<>();
        for (int i=0; i < randomNumber; i++) {
            Image image = Image.builder()
                .fileName("P_20241006005731_test.jpeg")
                .ord(i)
                .build();
            images.add(image);
        }

        PickUp pickup = PickUp.builder()
                .submitter(findWorker)
                .pickUpPlace(pickUpPlace)
                .latitude(129.1572648115502)
                .longitude(35.15768265599188)
                .mainTrashType(TrashType.대형_투기쓰레기류)
                .submitDateTime(LocalDateTime.now())
                .realTrashAmount(random.nextInt(1, 10))
                .images(images)
                .build();

        // 3. PickUp 엔티티 리포지토리에 저장
        PickUp savedPickup = pickUpRepository.save(pickup);

        // 4. 결과 검증
        assertNotNull(savedPickup);
        assertNotNull(savedPickup.getId());
        log.info("집하지 등록이 성공적으로 저장되었습니다. ID: {}", savedPickup.getId());
    }

    @Test
    @DisplayName("랜덤 PickUp 100개 추가 테스트 - 이미지 fileName도 추가")
    @Commit
    void testRandomPickUpInsert100() {
        List<Worker> submitter = memberRepository.findAll().stream()
            .filter(member -> member instanceof Worker)
            .map(member -> (Worker) member)
            .collect(Collectors.toList());

//        List<Beach> beaches = beachRepository.findAll();

        Random random = new Random();

        List<String> pickUpPlaces = Arrays.asList(
            "해수욕장 입구",
            "해변 산책로",
            "등대 주변",
            "해안 전망대",
            "해변 공원",
            "해변 근처 기차역",
            "해안가 상업지구",
            "해변 문화센터",
            "마리나",
            "해양 생물 관람장",
            "해변 컨벤션 센터",
            "해변 거리",
            "해안 시장",
            "해변 메인 도로",
            "해변 둘레길",
            "인근 소규모 해변",
            "해안 습지",
            "해변 근처 대형 마트",
            "해변 주차장",
            "해변 캠핑장"
        );

        for (int i = 0; i < 100 ; i++) {
            Worker randomSubmitter = submitter.get(random.nextInt(submitter.size()));

            Admin managedAdmin = (Admin) memberRepository.findById(randomSubmitter.getManagerId())
                .orElseThrow(() -> new NoSuchElementException("Admin with id "+ randomSubmitter.getManagerId() +" not found"));

            List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();

            Beach randomBeach = beachRepository.findById(assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
                .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(random.nextInt(assignmentAreaList.size()))));

            String randomPlace = pickUpPlaces.get(random.nextInt(pickUpPlaces.size()));

            // 지정된 범위 내에서 임의의 날짜를 생성합니다.
            LocalDate startDate = LocalDate.of(2022, 2, 1);
            LocalDate endDate = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            LocalDate randomDate = startDate.plusDays(random.nextInt((int) daysBetween + 1));

            // 필요한 경우 월을 조정하세요.
            if (randomDate.getMonthValue() == 12 || randomDate.getMonthValue() == 1) {
                randomDate = randomDate.withMonth(random.nextInt(2, 12)); //2월부터 11월까지
            }

            LocalDateTime randomSubmitDateTime = LocalDateTime.of(
                randomDate,
                LocalTime.of(random.nextInt(24), random.nextInt(60))
            );

            int randomNumber = random.nextInt(1, 4);
            List<Image> images = new ArrayList<>();
            for (int j=0; j < randomNumber; j++) {
                Image image = Image.builder()
                    .fileName("P_20241006005731_test.jpeg")
                    .ord(j)
                    .build();
                images.add(image);
            }

            double randomOffsetLat = (random.nextDouble() - 0.5) * 0.001; // -0.0005 ~ +0.0005
            double randomOffsetLon = (random.nextDouble() - 0.5) * 0.001; // -0.0005 ~ +0.0005

            double startLat = randomBeach.getLatitude() + randomOffsetLat;
            double startLon = randomBeach.getLongitude() + randomOffsetLon;



            PickUp pickup = PickUp.builder()
                .submitter(randomSubmitter)
                .pickUpPlace(randomPlace)
                .latitude(startLat)
                .longitude(startLon)
                .mainTrashType(TrashType.values()[random.nextInt(TrashType.values().length)])
                .submitDateTime(randomSubmitDateTime) // 최근 24시간 내의 랜덤 시간
                .realTrashAmount(random.nextInt(1, 5)) // 50.0에서 250.0 사이의 랜덤 값
                .images(images)
                .build();

            log.info("pickup : " + pickup.toString());

            pickUpRepository.save(pickup);
        }
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
        log.info("findPickUpWithAdmin : " + findPickUpWithAdminAndImages.get(1).getImages());

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
    // ---------- findByIdWithImage 조회 테스트 시작 -----------
    @Test
    @DisplayName("findByIdWithImage 조회 테스트")
    void testFindByIdWithImage() {
        Long pickUpId = 33L;

        PickUp findPickUp = pickUpRepository.findByIdWithImage(pickUpId)
            .orElseThrow(() -> new EntityNotFoundException("해당 PickUp을 찾을 수 없습니다. : " + pickUpId));

        log.info("findByIdWithImage : " + findPickUp.toString());
        log.info("findByIdWithImage : " + findPickUp.getImages().toString());
    }
    // ---------- findByIdWithImage 조회 테스트 끝-----------

}