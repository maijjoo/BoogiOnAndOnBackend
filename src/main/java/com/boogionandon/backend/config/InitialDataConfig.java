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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        
        // create 때에는 다 지워졌기 때문에 없음 beachService 같은걸 쓸 수 없음....
        
        String si = "부산광역시";
        
        List<String> guGunList = Arrays.asList("강서구", "기장군", "사하구", "수영구", "영도구");
        
        Map<String, List<String>> beachNameMap = new HashMap<>();
        
        beachNameMap.put("강서구", Arrays.asList("천선항"));
        
        beachNameMap.put("기장군", Arrays.asList(
                "공수항방파제", "길천 방파제", "대변항", "동백항", "동암항",
                "문동항 방파제", "문중항 방파제", "서암항 방파제", "온정 마을회관",
                "월내 방파제", "월전항", "이동항 방파제", "이천 방파제",
                "일광해수욕장", "임랑해수욕장", "죽성항", "칠암 방파제", "학리항"
        ));
        
        beachNameMap.put("사하구", Arrays.asList("다대포해수욕장"));
        
        beachNameMap.put("수영구", Arrays.asList(
                "광안리해수욕장", "남천항", "민락항"
        ));
        
        beachNameMap.put("영도구", Arrays.asList(
                "감지해변", "아치해변", "중리항", "하리항", "흰여울문화마을"
        ));
        
        if (memberRepository.count() == 0) {
            for (int i = 1; i <= 4; i++) {
                
                List<String> assignmentAreaList = Arrays.asList(
                        // 강서구
                        "천선항",
                        
                        // 기장군
                        "공수항방파제", "동암항", "서암항 방파제", "대변항", "월전항", "죽성항", "학리항",
                        "일광해수욕장", "이동항 방파제", "온정 마을회관", "동백항", "칠암 방파제",
                        "문중항 방파제", "문동항 방파제", "임랑해수욕장", "월내 방파제", "길천 방파제",
                        "이천 방파제",
                        
                        // 사하구
                        "다대포해수욕장",
                        
                        // 수영구
                        "광안리해수욕장", "민락항", "남천항",
                        
                        // 영도구
                        "하리항", "중리항", "감지해변", "흰여울문화마을", "아치해변"
                );
                
                List<String> nameList = Arrays.asList("김재원", "라주엽", "송지현", "이석현");
                
                Admin admin = Admin.builder()
                        .username("S_Busan" + i)
                        .password(passwordEncoder.encode("0000"))
                        .email("superadmin" + i + "@ocean.net")
                        .name(nameList.get(i - 1))
                        .phone("010-1111-111" + i)
                        .address("부산 연제구")
                        .addressDetail("중앙대로 1001 부산광역시청")
                        .workCity(si)
                        .workPlace("부산 시청")
                        .department("해양농수산국") // 일단 할거 같은 곳
                        .position("공무원") // 어떤 직급이 할지 모르겠음
                        .assignmentAreaList(assignmentAreaList) // 수퍼 관리자라 부산 전체로 잡음
                        .contact("051-1111-111" + i)
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
            
            List<String> nameList = Arrays.asList("안철수", "조국", "추미애", "문재인", "이명박");
            
            for (int i = 0; i < guGunList.size(); i++) {
                
                Admin admin = Admin.builder()
                        .username(i == 0 ? "A_testAdmin" : "A_testAdmin" + i)
                        .password(passwordEncoder.encode("0000"))
                        .email(i == 0 ? "test@admin.com" : "test" + i + "@admin.com")
                        .name(nameList.get(i))
                        .phone("010-9999-999" + i)
                        .address("부산 광역시 수영구")
                        .addressDetail("수영" + (i + 1) + "동 10" + (i + 1) + "번지")
                        .managerId(1L) // Super Admin1
                        .workCity(si)
                        .workPlace(guGunList.get(i))
                        .department("해양수산")
                        .position("공무원") // 직급체계 잘 모름
                        .assignmentAreaList(beachNameMap.get(guGunList.get(i)))
                        .contact("051-9999-999" + i)
                        .build();
                
                admin.getMemberRoleList().add(MemberType.ADMIN);
                adminRepository.save(admin);
                log.info("Admin created");
            }
        }
        
        if (memberRepository.count() == 9) {
            Random random = new Random();
            int randomValue = random.nextInt(0, (guGunList.size() * 10));
            
            List<String> nameList = Arrays.asList(
                    "김민서", "이준호", "박소연", "정태윤", "최예은",
                    "강동훈", "윤서아", "조현우", "신지원", "임수빈",
                    "한태희", "오민준", "서예린", "권지호", "노은서",
                    "유진혁", "백서윤", "송민재", "황다은", "전현준",
                    "고은주", "남기태", "문소율", "양준서", "홍지아",
                    "장민호", "허은지", "안서준", "배지훈", "추미래",
                    "구자윤", "차현우", "염지민", "주성민", "나은채",
                    "석호준", "옥지안", "마동석", "봉미선", "성태양",
                    "국민호", "방지현", "피도윤", "탁서연", "하윤서",
                    "진서우", "백이안", "심규리", "예지원", "채호진",
                    "김도영", "이하은", "박재현", "정민아", "최우진",
                    "강서연", "윤태호", "조미래", "신동현", "임하린",
                    "한승우", "오지은", "서준영", "권유진", "노태현",
                    "유소민", "백동훈", "송지원", "황준호", "전수빈",
                    "고민준", "남유나", "문승훈", "양지현", "홍서영",
                    "장예준", "허재원", "안소율", "배현서", "추성민",
                    "구민지", "차윤서", "염동휘", "주하늘", "나준혁",
                    "석민서", "옥태원", "마유진", "봉지훈", "성예림",
                    "국서진", "방윤호", "피하준", "탁민영", "하승민",
                    "진유리", "모태준", "심지우", "예성준"
            );
            
            List<Long> regularAdminList = Arrays.asList(5L, 6L, 7L, 8L, 9L);
            
            for (int i = 0; i < (guGunList.size() * 20) - 1; i++) {
                if (i < 70) {
                    
                    int currentYear = LocalDate.now().getYear();
                    int birthYear = currentYear - random.nextInt(21) - 40; // 40~60세 사이의 랜덤한 나이
                    int birthMonth = random.nextInt(12) + 1; // 1~12월
                    int birthDay = random.nextInt(28) + 1; // 간단히 1~28일로 설정 (월별 일수 차이 무시)
                    
                    LocalDate birth = LocalDate.of(birthYear, birthMonth, birthDay);
                    
                    Worker worker = Worker.builder()
                            .username(i == 0 ? "W_testWorker" : "W_testWorker" + i)
                            .password(passwordEncoder.encode("0000"))
                            .email("test" + i + "@worker.com")
                            .name(nameList.get(i))
                            .phone("010-8888-88" + (i < 10 ? "0" + i : i))
                            .birth(birth)
                            .address("부산 광역시 수영구")
                            .addressDetail("수영3동 301번지")
                            .managerId(regularAdminList.get(
                                    random.nextInt(regularAdminList.size()))) // 위의 테스트에서 만들어진 Admin
                            .startDate(LocalDate.now()) // 실제는 화면에서 선택
                            .endDate(LocalDate.now().plusMonths(6)) // 실제는 화면에서 선택
                            .build();
                    worker.getMemberRoleList().add(MemberType.WORKER);
                    log.info("WorkerWithOutCar : " + worker);
                    workerRepository.save(worker);
                } else {
                    
                    int currentYear = LocalDate.now().getYear();
                    int birthYear = currentYear - random.nextInt(21) - 40; // 40~60세 사이의 랜덤한 나이
                    int birthMonth = random.nextInt(12) + 1; // 1~12월
                    int birthDay = random.nextInt(28) + 1; // 간단히 1~28일로 설정 (월별 일수 차이 무시)
                    
                    LocalDate birth = LocalDate.of(birthYear, birthMonth, birthDay);
                    
                    Worker worker = Worker.builder()
                            .username(i == 0 ? "W_testWorker" : "W_testWorker" + i)
                            .password(passwordEncoder.encode("0000"))
                            .email("test" + i + "@worker.com")
                            .name(nameList.get(i))
                            .phone("010-8888-88" + (i < 10 ? "0" + i : i))
                            .birth(birth)
                            .address("부산 광역시 수영구")
                            .addressDetail("수영3동 302번지")
                            .managerId(regularAdminList.get(
                                    random.nextInt(regularAdminList.size()))) // 위의 테스트에서 만들어진 Admin
                            .startDate(LocalDate.now()) // 실제는 화면에서 선택
                            .endDate(LocalDate.now().plusMonths(6)) // 실제는 화면에서 선택
                            .vehicleCapacity(1.0 + (random.nextInt(5) * 0.5))  // 1톤부터 3톤까지 0.5톤 단위
                            .build();
                    worker.getMemberRoleList().add(MemberType.WORKER);
                    log.info("WorkerWithOutCar : " + worker);
                    workerRepository.save(worker);
                }
            }
        } else {
            log.info("Worker already exists");
        }
    }
    
    @PostConstruct
    @Transactional
    public void initBeaches() {
        if (beachRepository.count() == 0) {
            List<Beach> beaches = Arrays.asList(
                    // 강서구
                    Beach.builder().beachName("천선항").si("부산광역시").guGun("강서구").dongEub("천성동")
                            .workplace("강서구").latitude(35.026391).longitude(128.815480).build(),
                    
                    // 기장군
                    Beach.builder().beachName("공수항방파제").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.183368).longitude(129.213691).build(),
                    Beach.builder().beachName("동암항").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.196143).longitude(129.224846).build(),
                    Beach.builder().beachName("서암항 방파제").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.213799).longitude(129.223616).build(),
                    Beach.builder().beachName("대변항").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.224804).longitude(129.228315).build(),
                    Beach.builder().beachName("월전항").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.237419).longitude(129.245150).build(),
                    Beach.builder().beachName("죽성항").si("부산광역시").guGun("기장군").dongEub("기장읍")
                            .workplace("기장군").latitude(35.242838).longitude(129.247397).build(),
                    Beach.builder().beachName("학리항").si("부산광역시").guGun("기장군").dongEub("일광읍")
                            .workplace("기장군").latitude(35.258669).longitude(129.244704).build(),
                    Beach.builder().beachName("일광해수욕장").si("부산광역시").guGun("기장군").dongEub("일광읍")
                            .workplace("기장군").latitude(35.259783).longitude(129.233967).build(),
                    Beach.builder().beachName("이동항 방파제").si("부산광역시").guGun("기장군").dongEub("일광읍")
                            .workplace("기장군").latitude(35.269948).longitude(129.246561).build(),
                    Beach.builder().beachName("온정 마을회관").si("부산광역시").guGun("기장군").dongEub("일광읍")
                            .workplace("기장군").latitude(35.283389).longitude(129.256583).build(),
                    Beach.builder().beachName("동백항").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.288207).longitude(129.257192).build(),
                    Beach.builder().beachName("칠암 방파제").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.298369).longitude(129.261970).build(),
                    Beach.builder().beachName("문중항 방파제").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.304291).longitude(129.261368).build(),
                    Beach.builder().beachName("문동항 방파제").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.305628).longitude(129.259923).build(),
                    Beach.builder().beachName("임랑해수욕장").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.318495).longitude(129.264315).build(),
                    Beach.builder().beachName("월내 방파제").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.325770).longitude(129.278452).build(),
                    Beach.builder().beachName("길천 방파제").si("부산광역시").guGun("기장군").dongEub("장안읍")
                            .workplace("기장군").latitude(35.325749).longitude(129.284453).build(),
                    Beach.builder().beachName("이천 방파제").si("부산광역시").guGun("기장군").dongEub("일광읍")
                            .workplace("기장군").latitude(35.263531).longitude(129.239868).build(),
                    
                    // 사하구
                    Beach.builder().beachName("다대포해수욕장").si("부산광역시").guGun("사하구").dongEub("다대동")
                            .workplace("사하구").latitude(35.046288).longitude(128.963135).build(),
                    
                    // 수영구
                    Beach.builder().beachName("광안리해수욕장").si("부산광역시").guGun("수영구").dongEub("광안동")
                            .workplace("수영구").latitude(35.153169).longitude(129.118657).build(),
                    Beach.builder().beachName("민락항").si("부산광역시").guGun("수영구").dongEub("민락동")
                            .workplace("수영구").latitude(35.152851).longitude(129.128165).build(),
                    Beach.builder().beachName("남천항").si("부산광역시").guGun("수영구").dongEub("남천동")
                            .workplace("수영구").latitude(35.138682).longitude(129.114372).build(),
                    
                    // 영도구
                    Beach.builder().beachName("하리항").si("부산광역시").guGun("영도구").dongEub("동삼동")
                            .workplace("영도구").latitude(35.069812).longitude(129.081134).build(),
                    Beach.builder().beachName("중리항").si("부산광역시").guGun("영도구").dongEub("동삼동")
                            .workplace("영도구").latitude(35.068405).longitude(129.064921).build(),
                    Beach.builder().beachName("감지해변").si("부산광역시").guGun("영도구").dongEub("동삼동")
                            .workplace("영도구").latitude(35.059898).longitude(129.077522).build(),
                    Beach.builder().beachName("흰여울문화마을").si("부산광역시").guGun("영도구").dongEub("영선동")
                            .workplace("영도구").latitude(35.078205).longitude(129.044827).build(),
                    Beach.builder().beachName("아치해변").si("부산광역시").guGun("영도구").dongEub("동삼동")
                            .workplace("영도구").latitude(35.076156).longitude(129.092375).build()
            );
            
            beachRepository.saveAll(beaches);
        }
    }

// 아래 데이터는 언젠가 확대 되길 바라며.....
//  @PostConstruct
//  @Transactional
//  public void initBeaches() {
//    if (beachRepository.count() == 0) {
//      List<Beach> beaches = Arrays.asList(
//          // 공식 해수욕장
//          Beach.builder().beachName("해운대해수욕장").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1586).longitude(129.1603).build(),
//          Beach.builder().beachName("광안리해수욕장").si("부산광역시").guGun("수영구").dongEub("광안동").workplace("수영구").latitude(35.1532).longitude(129.1182).build(),
//          Beach.builder().beachName("송정해수욕장").si("부산광역시").guGun("해운대구").dongEub("송정동").workplace("해운대구").latitude(35.1785).longitude(129.1994).build(),
//          Beach.builder().beachName("다대포해수욕장").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
//          Beach.builder().beachName("송도해수욕장").si("부산광역시").guGun("서구").dongEub("암남동").workplace("서구").latitude(35.0764).longitude(129.0243).build(),
//          Beach.builder().beachName("일광해수욕장").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
//          Beach.builder().beachName("임랑해수욕장").si("부산광역시").guGun("기장군").dongEub("장안읍").workplace("기장군").latitude(35.3184).longitude(129.2646).build(),
//
//          // 기타 해변 및 해안가
//          Beach.builder().beachName("감지해변").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2741).longitude(129.2437).build(),
//          Beach.builder().beachName("국립부산과학관 해변").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
//          Beach.builder().beachName("다선해변").si("부산광역시").guGun("영도구").dongEub("청학동").workplace("영도구").latitude(35.0891).longitude(129.0746).build(),
//          Beach.builder().beachName("몰운대").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0411).longitude(128.9663).build(),
//          Beach.builder().beachName("미포").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1716).build(),
//          Beach.builder().beachName("송림해변").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2678).longitude(129.2384).build(),
//          Beach.builder().beachName("암남공원").si("부산광역시").guGun("서구").dongEub("암남동").workplace("서구").latitude(35.0679).longitude(129.0193).build(),
//          Beach.builder().beachName("오륙도").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1016).longitude(129.1225).build(),
//          Beach.builder().beachName("이기대").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1166).longitude(129.1225).build(),
//          Beach.builder().beachName("일광해안").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
//          Beach.builder().beachName("장안사계해변").si("부산광역시").guGun("기장군").dongEub("장안읍").workplace("기장군").latitude(35.3184).longitude(129.2646).build(),
//          Beach.builder().beachName("죽성성게마을").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
//          Beach.builder().beachName("청사포").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1908).build(),
//          Beach.builder().beachName("태종대").si("부산광역시").guGun("영도구").dongEub("동삼동").workplace("영도구").latitude(35.0532).longitude(129.0854).build(),
//
//          // 포구 및 항구
//          Beach.builder().beachName("감천항").si("부산광역시").guGun("사하구").dongEub("감천동").workplace("사하구").latitude(35.0813).longitude(129.0022).build(),
//          Beach.builder().beachName("기장항").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.1924).longitude(129.2124).build(),
//          Beach.builder().beachName("남포동").si("부산광역시").guGun("중구").dongEub("남포동").workplace("중구").latitude(35.0987).longitude(129.0317).build(),
//          Beach.builder().beachName("다대포항").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
//          Beach.builder().beachName("부산항").si("부산광역시").guGun("영도구").dongEub("남항동").workplace("영도구").latitude(35.0967).longitude(129.0358).build(),
//          Beach.builder().beachName("영도").si("부산광역시").guGun("영도구").dongEub("남항동").workplace("영도구").latitude(35.0910).longitude(129.0444).build(),
//          Beach.builder().beachName("용호부두").si("부산광역시").guGun("남구").dongEub("용호동").workplace("남구").latitude(35.1166).longitude(129.1225).build(),
//          Beach.builder().beachName("자갈치시장").si("부산광역시").guGun("중구").dongEub("남포동").workplace("중구").latitude(35.0967).longitude(129.0304).build(),
//          Beach.builder().beachName("조도").si("부산광역시").guGun("사하구").dongEub("다대동").workplace("사하구").latitude(35.0479).longitude(128.9646).build(),
//          Beach.builder().beachName("충무동").si("부산광역시").guGun("중구").dongEub("충무동").workplace("중구").latitude(35.1015).longitude(129.0293).build(),
//
//          // 추가된 항구 및 해안가
//          Beach.builder().beachName("천성항").si("부산광역시").guGun("강서구").dongEub("천성동").workplace("강서구").latitude(35.0494).longitude(128.8147).build(),
//          Beach.builder().beachName("명지항").si("부산광역시").guGun("강서구").dongEub("명지동").workplace("강서구").latitude(35.0761).longitude(128.9028).build(),
//          Beach.builder().beachName("대변항").si("부산광역시").guGun("기장군").dongEub("기장읍").workplace("기장군").latitude(35.2235).longitude(129.2259).build(),
//          Beach.builder().beachName("칠암항").si("부산광역시").guGun("기장군").dongEub("일광읍").workplace("기장군").latitude(35.2605).longitude(129.2306).build(),
//          Beach.builder().beachName("미포항").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1628).longitude(129.1716).build(),
//          Beach.builder().beachName("민락수변공원").si("부산광역시").guGun("수영구").dongEub("민락동").workplace("수영구").latitude(35.1563).longitude(129.1284).build(),
//          Beach.builder().beachName("송정항").si("부산광역시").guGun("해운대구").dongEub("송정동").workplace("해운대구").latitude(35.1785).longitude(129.1994).build(),
//          Beach.builder().beachName("동백섬").si("부산광역시").guGun("해운대구").dongEub("우동").workplace("해운대구").latitude(35.1531).longitude(129.1530).build(),
//          Beach.builder().beachName("남천동 해안").si("부산광역시").guGun("수영구").dongEub("남천동").workplace("수영구").latitude(35.1403).longitude(129.1148).build(),
//          Beach.builder().beachName("영도 깍천").si("부산광역시").guGun("영도구").dongEub("동삼동").workplace("영도구").latitude(35.0789).longitude(129.0876).build(),
//          Beach.builder().beachName("아미산 전망대").si("부산광역시").guGun("사하구").dongEub("감천동").workplace("사하구").latitude(35.0900).longitude(129.0069).build(),
//          Beach.builder().beachName("용두산공원").si("부산광역시").guGun("중구").dongEub("광복동").workplace("중구").latitude(35.1010).longitude(129.0325).build()
//      );
//
//      beachRepository.saveAll(beaches);
//    }
//  }
}
