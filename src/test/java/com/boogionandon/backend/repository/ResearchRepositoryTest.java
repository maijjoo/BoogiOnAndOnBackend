package com.boogionandon.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Image;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.ResearchSub;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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
    
    // --------- insert 테스트 시작 ------------
    @Test
    @DisplayName("research 추가 테스트 - 같이 일한 팀원 없음, 혼자 했다고 할때")
    @Commit
    void testResearchInsert() {
        Random random = new Random();
        Long researcherId = 11L; // initData에서 만들어진 Worer id => 11L
        Worker researcher = (Worker) memberRepository.findById(researcherId)
                .orElseThrow(() -> new NoSuchElementException("Worker with id " + researcherId + " not found"));
        // initData에서 만든 Worker의 id
        
        Admin managedAdmin = (Admin) memberRepository.findById(researcher.getManagerId())
                .orElseThrow(
                        () -> new NoSuchElementException("Admin with id " + researcher.getManagerId() + " not found"));
        
        List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();
        
        Beach randomBeach = beachRepository.findById(assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(random.nextInt(assignmentAreaList.size()))));
        
        int randomNumber = random.nextInt(1 + 27) + 3;
        List<Image> images = new ArrayList<>();
        for (int j = 0; j < randomNumber; j++) {
            Image image = Image.builder()
                    .fileName("R_20241006005731_test.jpeg")
                    .ord(j)
                    .build();
            images.add(image);
        }
        
        // 리서치 메인 먼저 만들고 그다음 서브 넣을 예정
        ResearchMain researchMain = ResearchMain.builder()
                .researcher(researcher)
                .beach(randomBeach)
                .expectedTrashAmount(150) // L
                .reportTime(LocalDateTime.now()) // 리포트 작성 시간
                // 이미지는 여기선 일단 패스
                .status(ReportStatus.ASSIGNMENT_NEEDED)
                .weather("맑음") // 오늘 날씨
                .specialNote("태풍") // 보고 올릴때 쓰레기가 특수 상황에 의해 발생한 것인지
                .totalBeachLength(0.0)
                .images(images)
                .build();
        
        log.info("researchMain : " + researchMain);
        
        ResearchMain savedResearchMain = researchMainRepository.save(researchMain);
        
        Double totalBeachLength = 0.0;
        
        for (int i = 1; i <= 4; i++) {
            ResearchSub researchSub = ResearchSub.builder()
                    .research(savedResearchMain)
                    .beachNameWithIndex(randomBeach.getBeachName() + i)
                    // 반복으로 하니까 위, 경도 바꾸기가 쉽지않아 일단 동일하게 받음
                    .startLatitude(35.15768265599188)
                    .startLongitude(129.1572648115502)
                    .endLatitude(35.15779193363473)
                    .endLongitude(129.15770660944662)
                    .mainTrashType(TrashType.대형_투기쓰레기류)
                    // researchLength는 실제로 넣을 때는 위의 위,경도를 계산해서 넣기
                    .researchLength(DistanceCalculator.calculateDistance(35.15768265599188, 129.1572648115502,
                            35.15779193363473, 129.15770660944662))
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
    @DisplayName("100개 random insert 테스트 - 이미지 fileName 추가")
    @Transactional
    @Commit
    void testCreateRandomResearches() {
        List<Worker> researchers = memberRepository.findAll().stream()
                .filter(member -> member instanceof Worker)
                .map(member -> (Worker) member)
                .collect(Collectors.toList());
        
        Random random = new Random();
        
        for (int i = 0; i < 100; i++) {
            Worker randomResearcher = researchers.get(random.nextInt(researchers.size()));
            
            Admin managedAdmin = (Admin) memberRepository.findById(randomResearcher.getManagerId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Admin with id " + randomResearcher.getManagerId() + " not found"));
            
            List<String> assignmentAreaList = managedAdmin.getAssignmentAreaList();
            
            Beach randomBeach = beachRepository.findById(
                            assignmentAreaList.get(random.nextInt(assignmentAreaList.size())))
                    .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + assignmentAreaList.get(
                            random.nextInt(assignmentAreaList.size()))));
            
            // 같이 일하는 인원을 추가 시키기 위한 코드
            List<String> memberList = researchers.stream()
                    .filter(member -> member.getManagerId().equals(randomResearcher.getManagerId()) && !member.equals(
                            randomResearcher))
                    .map(member -> member.getName() + " " + member.getPhone()
                            .substring(member.getPhone().lastIndexOf("-") + 1))
                    .collect(Collectors.toList());
            
            String members = "";
            
            if (!memberList.isEmpty()) {
                int randomTo = random.nextInt(1, 6);
                List<String> selectedMembers = new ArrayList<>(memberList);
                Collections.shuffle(selectedMembers);
                members = selectedMembers.subList(0, randomTo).stream()
                        .collect(Collectors.joining(","));
            }
            
            // 지정된 범위 내에서 임의의 날짜를 생성합니다.
            LocalDate startDate = LocalDate.of(2022, 2, 1);
            LocalDate endDate = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            LocalDate randomDate = startDate.plusDays(random.nextInt((int) daysBetween + 1));
            
            // 필요한 경우 월을 조정하세요.
            if (randomDate.getMonthValue() == 12 || randomDate.getMonthValue() == 1) {
                randomDate = randomDate.withMonth(random.nextInt(2, 12)); //2월부터 11월까지
            }
            
            LocalDateTime randomReportTime = LocalDateTime.of(
                    randomDate,
                    LocalTime.of(random.nextInt(24), random.nextInt(60))
            );
            
            int randomNumber = random.nextInt(1, 27) + 3;
            List<Image> images = new ArrayList<>();
            for (int j = 0; j < randomNumber; j++) {
                Image image = Image.builder()
                        .fileName("R_20241006005731_test.jpeg")
                        .ord(i)
                        .build();
                images.add(image);
            }
            
            String[] weatherOptions = {"맑음", "흐림", "비", "눈", "안개"};
            String randomWeather = weatherOptions[random.nextInt(weatherOptions.length)];
            
            String[] specialNotes = {"없음", "태풍", "홍수", "집중호우", "폭풍 해일"};
            String randomSpecialNote = specialNotes[random.nextInt(specialNotes.length)];
            
            ResearchMain researchMain = ResearchMain.builder()
                    .researcher(randomResearcher)
                    .beach(randomBeach)
                    .expectedTrashAmount(random.nextInt(50, 100))
                    .reportTime(randomReportTime)
                    .images(images)
                    .status(ReportStatus.ASSIGNMENT_NEEDED)
                    .weather(randomWeather)
                    .specialNote(randomSpecialNote)
                    .totalBeachLength(0.0)
                    .members(members)
                    .build();
            
            ResearchMain savedResearchMain = researchMainRepository.save(researchMain);
            
            Double totalBeachLength = 0.0;
            
            for (int j = 1; j <= 4; j++) {
                double randomOffset = (random.nextDouble() - 0.5) * 0.002; // 대략 100-200 meters
                
                double startLat = randomBeach.getLatitude() + randomOffset;
                double startLon = randomBeach.getLongitude() + randomOffset;
                double endLat = startLat + (random.nextDouble() - 0.5) * 0.0002; // 대략 10-20 meters
                double endLon = startLon + (random.nextDouble() - 0.5) * 0.0002;
                
                ResearchSub researchSub = ResearchSub.builder()
                        .research(savedResearchMain)
                        .beachNameWithIndex(randomBeach.getBeachName() + j)
                        .startLatitude(startLat)
                        .startLongitude(startLon)
                        .endLatitude(endLat)
                        .endLongitude(endLon)
                        .mainTrashType(TrashType.values()[random.nextInt(TrashType.values().length)])
                        .researchLength(DistanceCalculator.calculateDistance(startLat, startLon, endLat, endLon))
                        .build();
                
                ResearchSub savedResearchSub = researchSubRepository.save(researchSub);
                
                totalBeachLength += DistanceCalculator.calculateDistance(
                        researchSub.getStartLatitude(), researchSub.getStartLongitude(),
                        researchSub.getEndLatitude(), researchSub.getEndLongitude()
                );
                
                savedResearchMain.getResearchSubList().add(savedResearchSub);
            }
            
            savedResearchMain.setTotalResearch(totalBeachLength);
            researchMainRepository.save(savedResearchMain);
        }
    }
    
    // --------- insert 테스트 끝 ------------
    
    
    @Test
    @DisplayName("research 조회 테스트")
    void testResearchRead() {
        Long researchMainId = 1L;
        
        ResearchMain researchMain = researchMainRepository.findById(researchMainId)
                .orElseThrow(() -> new NoSuchElementException("ResearchMain with id " + researchMainId + " not found"));
        
        // ToString으로 볼때는 무한 루프 빠질거 같은 건 빼놓았음
        log.info("researchMain : " + researchMain);
        log.info("researchSubs : " + researchMain.getResearchSubList());
    }
    
    // ------ findByStatusNeededAndSearch, findByStatusCompletedAndSearch 시작 ------
    @Test
    @DisplayName("findByStatusNeededAndSearch 조회 테스트 - 수퍼와 일반")
    void testFindByStatusNeededAndSearch() {
        
        // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
        // admin -> 5L, 6L, 7L, 8L, 9L initData 에서 자동으로 만들어진 regular
        Long adminId = 8L;
        
        String beachSearch = "광안리";
        
        // 기본으로 사용
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .build();
        
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
                pageRequestDTO.getSort().equals("desc") ?
                        Sort.by("reportTime").descending() :
                        Sort.by("reportTime").ascending()
        );
        
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));
        
        log.info("admin role : " + admin.getMemberRoleList().toString());
        
        // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
        boolean isContainSuper = admin.getMemberRoleList().stream()
                .anyMatch(role -> role == MemberType.SUPER_ADMIN);
        
        if (isContainSuper) {
            log.info("SuperAdmin 들어음");
            Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusNeededAndSearchForSuper(
                    beachSearch, pageable);
//      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusNeededAndSearchForSuper("", pageable);
            
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper);
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper.getContent());
        } else {
            log.info("Admin 들어음");
            Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusNeededAndSearchForRegular(
                    beachSearch, pageable, adminId);
//      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusNeededAndSearchForRegular("", pageable, adminId);
            
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular);
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular.getContent());
        }
        
        
    }
    
    @Test
    @DisplayName("findByStatusCompletedAndSearch 조회 테스트")
    void testFindByStatusCompletedAndSearch() {
        
        // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
        // admin -> 5L, 6L, 7L initData 에서 자동으로 만들어진 regular
        Long adminId = 8L;
        
        String beachSearch = "광안리";
        
        // 기본으로 사용
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .build();
        
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
                pageRequestDTO.getSort().equals("desc") ?
                        Sort.by("reportTime").descending() :
                        Sort.by("reportTime").ascending()
        );
        
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));
        
        log.info("admin role : " + admin.getMemberRoleList().toString());
        
        // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
        boolean isContainSuper = admin.getMemberRoleList().stream()
                .anyMatch(role -> role == MemberType.SUPER_ADMIN);
        
        if (isContainSuper) {
            log.info("SuperAdmin 들어음");
//      Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
            Page<ResearchMain> byStatusNeededAndSearchForSuper = researchMainRepository.findByStatusCompletedAndSearchForSuper(
                    "", pageable);
            
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper);
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForSuper.getContent());
        } else {
            log.info("Admin 들어음");
//      Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
            Page<ResearchMain> byStatusNeededAndSearchForRegular = researchMainRepository.findByStatusCompletedAndSearchForRegular(
                    "", pageable, adminId);
            
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular);
            log.info("byStatusNeededAndSearch : " + byStatusNeededAndSearchForRegular.getContent());
        }
    }
    
    
    // ------ findByStatusNeededAndSearch 끝 ------
    // ------ findByIdWithOutSub, findListByMainId 시작 --------
    @Test
    @DisplayName("findByIdWithOutSub, findListByMainId 조회 테스트")
    void testFindByIdWithOutSubAndFindListByMainId() {
        
        Long researchMainId = 1L;
        
        ResearchMain findMain = researchMainRepository.findByIdWithOutSub(researchMainId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Research를 찾을 수 없습니다. : " + researchMainId));
        log.info("findMain : " + findMain.toString());
        
        List<ResearchSub> findSubList = researchSubRepository.findListByMainId(researchMainId);
        log.info("findSubList : " + findSubList);
        
        
    }
    // ------ findByIdWithOutSub, findListByMainId 끝 --------
    
    // ------ findByDateCriteria 메서드 테스트 시작 ---------
    
    @Test
    @DisplayName("findByDateCriteria 메서드 테스트")
    void testFindByDateCriteria() {
        
        Integer year = 2024;
        Integer month = 2;
        LocalDate start = LocalDate.of(2023, 1, 5);
        LocalDate end = LocalDate.of(2023, 3, 31);

//    List<ResearchMain> findList = researchMainRepository.findByDateCriteria(year, null, null, null);
//    List<ResearchMain> findList = researchMainRepository.findByDateCriteria(year, month, null, null);
        List<ResearchMain> findList = researchMainRepository.findByDateCriteria(null, null, start, end);
        
        log.info("findList : " + findList);
        log.info("findList.size() : " + findList.size());
        
    }
    // ------ findByDateCriteria 메서드 테스트 끝 ---------
}