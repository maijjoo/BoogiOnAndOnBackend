package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.CleanDetailResponseDTO;
import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.DaysDataForTheMonthDTO;
import com.boogionandon.backend.dto.admin.FiveYearAgoToLastYearDTO;
import com.boogionandon.backend.dto.admin.MonthlyDataForTheYearDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.CleanRepository;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CleanLocalServiceImpl implements CleanService{

  private final CleanRepository cleanRepository;
  private final MemberRepository memberRepository;
  private final BeachRepository beachRepository;

  // clean 보고서 제출
  @Override
  public void insertClean(CleanRequestDTO cleanRequestDTO) {

    Clean clean = createCleanFromDTO(cleanRequestDTO);

    cleanRepository.save(clean);
  }

  // 관리자 페이지에서 쓰레기 분포도 볼때 필요한 메서드
  @Override
  @Transactional(readOnly = true) // 어차피 조회용 이니까
  public List<TrashMapResponseDTO> getTrashDistribution(Integer year, Integer month, LocalDate start, LocalDate end) {
    List<Clean> cleanData = cleanRepository.findByDateCriteria(year, month, start, end);

    return cleanData.stream().map(clean -> {
      return TrashMapResponseDTO.builder()
          .id(clean.getId())
          .cleanerName(clean.getCleaner().getUsername())
          .beachName(clean.getBeach().getBeachName())
          .realTrashAmount(clean.getRealTrashAmount())
          .cleanDateTime(clean.getCleanDateTime())
          .mainTrashType(clean.getMainTrashType())
          .fixedLatitude(clean.getBeach().getLatitude())
          .fixedLongitude(clean.getBeach().getLongitude())
          .build();
    }).collect(Collectors.toList());
  }

  // 관리자 페이지에서 기초 통계 관련 데이터
  @Override
  @Transactional(readOnly = true) // 어차피 조회용 이니까
  public BasicStatisticsResponseDTO getBasicStatistics(String tapCondition, Integer year, Integer month, String beachName) {

    // 리포지토리에서 계속 안되서 서비스 레이어로 옮김 -> 왠지는 모르겠는데 빼니까 잘됨
    // 일별, 월별의 가장 처음에는 선택일 테니 이때는 작년을 보여주기
    year = year == null ? LocalDate.now().getYear() - 1 : year;
    // 일별에서 가장 처음에는 선택이니 12월을 보여주기
    month = month == null ? 12 : month;


    log.info("Service Layer - Start of method - tapCondition: {}, year: {}, month: {}, beachName: {}", tapCondition, year, month, beachName);
    List<Clean> findData = cleanRepository.getBasicStatistics(tapCondition, year, month, beachName);
    log.info("Service Layer - After repository call - cleanList size: {}", findData.size());

    BasicStatisticsResponseDTO responseDTO = new BasicStatisticsResponseDTO();

    switch (tapCondition) {
      case "연도별":
        List<FiveYearAgoToLastYearDTO> yearlyStats = processYearlyData(findData);
        responseDTO.setYears(yearlyStats);
        break;
      case "월별":
        List<MonthlyDataForTheYearDTO> monthlyStats = processMonthlyData(findData, year);
        responseDTO.setMonthly(monthlyStats);
        break;
      case "일별":
        List<DaysDataForTheMonthDTO> dailyStats = processDailyData(findData, year, month);
        responseDTO.setDays(dailyStats);
        break;
      default:
        throw new IllegalArgumentException("Invalid tapCondition: " + tapCondition);
    }

    return responseDTO;
  }

  @Override
  public Page<Clean> findResearchByStatusNeededAndSearch(String beachSearch, Pageable pageable, Long adminId) {
    // tapCondition은 컨트롤러에서 처리하기

    // 수퍼 관리자 인지 아닌지 판별
    // repository에서 결정 할까? 했지만 repository에서 repository를 import하는게 아닌거 같아서 여기서 나눔
    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
      return cleanRepository.findByStatusNeededAndSearchForSuper(beachSearch, pageable);
    } else {
      log.info("Admin 들어음");
      return cleanRepository.findByStatusNeededAndSearchForRegular(beachSearch, pageable, adminId);
    }
  }

  @Override
  public Page<Clean> findResearchByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId) {
    // 수퍼 관리자 인지 아닌지 판별
    // repository에서 결정 할까? 했지만 repository에서 repository를 import하는게 아닌거 같아서 여기서 나눔
    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
      return cleanRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
    } else {
      log.info("Admin 들어음");
      return cleanRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
    }
  }

  @Override
  public CleanDetailResponseDTO getCleanDetail(Long cleanId) {

    Clean clean = cleanRepository.findByIdWithImage(cleanId)
        .orElseThrow(() -> new EntityNotFoundException("해당 Clean을 찾을 수 없습니다. : " + cleanId));

    String members = clean.getMembers();

    List<String> memberList = Arrays.stream(members.split(",")).toList();

    return CleanDetailResponseDTO.builder()
        .id(clean.getId())
        .cleanerName(clean.getCleaner().getName())
        .beachName(clean.getBeach().getBeachName())
        .realTrashAmount(clean.getRealTrashAmount())
        .cleanDateTime(clean.getCleanDateTime())
        .startLatitude(clean.getStartLatitude())
        .startLongitude(clean.getStartLongitude())
        .endLatitude(clean.getEndLatitude())
        .endLongitude(clean.getEndLongitude())
        .beachLength(clean.getBeachLength())
        .mainTrashType(clean.getMainTrashType())
        .status(clean.getStatus())
        .images(clean.getImages().stream().map(image -> {
          return "S_" +image.getFileName();
        }).collect(Collectors.toList()))
        .members(memberList)
        .weather(clean.getWeather())
        .specialNote(clean.getSpecialNote())
        .build();
  }

  @Override
  public void updateStatus(Long cleanId) {

    //영속 처리 된것임
    Clean findClean = cleanRepository.findById(cleanId)
        .orElseThrow(() -> new EntityNotFoundException("해당 Clean을 찾을 수 없습니다. : " + cleanId));

    if(ReportStatus.ASSIGNMENT_NEEDED.equals(findClean.getStatus())) {
      log.info("상태 변경 시작: {}", findClean.getStatus());
      findClean.changeStatusToCompleted(ReportStatus.ASSIGNMENT_COMPLETED);
      log.info("상태 변경 완료: {}", findClean.getStatus());
    } else {
      throw new IllegalStateException("Can only change status when current status is ASSIGNMENT_NEEDED");
    }

  }
  // ------------getBasicStatistics 관련 메서드 시작-------
  /**
   * 연도별 쓰레기 통계 데이터를 처리합니다.
   */
  private List<FiveYearAgoToLastYearDTO> processYearlyData(List<Clean> findData) {
    Map<Integer, FiveYearAgoToLastYearDTO> fiveYearsStatsMap = new TreeMap<>();

    for (Clean clean : findData) {
      int year = clean.getCleanDateTime().getYear();
      FiveYearAgoToLastYearDTO yearStat = fiveYearsStatsMap.computeIfAbsent(year,
          k -> FiveYearAgoToLastYearDTO.builder().year(k).beachCount(0).build());

      yearStat.setBeachCount(yearStat.getBeachCount() + 1);
      updateTrashStatistics(yearStat, clean);
    }
    List<FiveYearAgoToLastYearDTO> result = new ArrayList<>(fiveYearsStatsMap.values());
    calculatePercentages(result);
    return result;
  }

  /**
   * 월별 쓰레기 통계 데이터를 처리합니다.
   */
  private List<MonthlyDataForTheYearDTO> processMonthlyData(List<Clean> findData, Integer year) {
    Map<Integer, MonthlyDataForTheYearDTO> monthlyStatsMap = new TreeMap<>();

    for (Clean clean : findData) {
      if (clean.getCleanDateTime().getYear() == year) {
        int month = clean.getCleanDateTime().getMonthValue();
        MonthlyDataForTheYearDTO monthStat = monthlyStatsMap.computeIfAbsent(month,
            k -> MonthlyDataForTheYearDTO.builder().month(k).surveyAreaCount(0).build());

        monthStat.setSurveyAreaCount(monthStat.getSurveyAreaCount() + 1);
        updateTrashStatistics(monthStat, clean);
      }
    }

    List<MonthlyDataForTheYearDTO> result = new ArrayList<>(monthlyStatsMap.values());
    calculatePercentages(result);
    return result;
  }

  /**
   * 일별 쓰레기 통계 데이터를 처리합니다.
   */
  private List<DaysDataForTheMonthDTO> processDailyData(List<Clean> findData, Integer year, Integer month) {
    Map<Integer, DaysDataForTheMonthDTO> dailyStatsMap = new TreeMap<>();

    for (Clean clean : findData) {
      LocalDateTime cleanTime = clean.getCleanDateTime();
      if (cleanTime.getYear() == year && cleanTime.getMonthValue() == month) {
        int day = cleanTime.getDayOfMonth();
        DaysDataForTheMonthDTO dayStat = dailyStatsMap.computeIfAbsent(day,
            k -> DaysDataForTheMonthDTO.builder().day(k).surveyAreaCount(0).build());

        dayStat.setSurveyAreaCount(dayStat.getSurveyAreaCount() + 1);
        updateTrashStatistics(dayStat, clean);
      }
    }

    List<DaysDataForTheMonthDTO> result = new ArrayList<>(dailyStatsMap.values());
    calculatePercentages(result);
    return result;
  }
  /**
   * 쓰레기 통계를 업데이트합니다.
   */
  private void updateTrashStatistics(Object stat, Clean clean) {
    double trashAmountTons = convertToTons(clean.getRealTrashAmount());

    switch (clean.getMainTrashType()) {
      case 폐어구류:
        setFieldValue(stat, "fishingGearWasteTons", getFieldValue(stat, "fishingGearWasteTons") + trashAmountTons);
        break;
      case 부표류:
        setFieldValue(stat, "buoyDebrisTons", getFieldValue(stat, "buoyDebrisTons") + trashAmountTons);
        break;
      case 생활쓰레기류:
        setFieldValue(stat, "householdWasteTons", getFieldValue(stat, "householdWasteTons") + trashAmountTons);
        break;
      case 대형_투기쓰레기류:
        setFieldValue(stat, "largeDisposalWasteTons", getFieldValue(stat, "largeDisposalWasteTons") + trashAmountTons);
        break;
      case 초목류:
        setFieldValue(stat, "vegetationWasteTons", getFieldValue(stat, "vegetationWasteTons") + trashAmountTons);
        break;
    }

    setFieldValue(stat, "totalTons", getFieldValue(stat, "totalTons") + trashAmountTons);
  }

  /**
   * 쓰레기양의 퍼센티지를 계산하고 문자열 형식으로 설정합니다.
   */
  private <T> void calculatePercentages(List<T> statsList) {
    for (T stat : statsList) {
      double totalTons = getFieldValue(stat, "totalTons");

      setStringField(stat, "buoyDebris", getFieldValue(stat, "buoyDebrisTons"), totalTons);
      setStringField(stat, "householdWaste", getFieldValue(stat, "householdWasteTons"), totalTons);
      setStringField(stat, "largeDisposalWaste", getFieldValue(stat, "largeDisposalWasteTons"), totalTons);
      setStringField(stat, "vegetationWaste", getFieldValue(stat, "vegetationWasteTons"), totalTons);
      setStringField(stat, "fishingGearWaste", getFieldValue(stat, "fishingGearWasteTons"), totalTons);

      setFieldValue(stat, "total", String.format("%.2ft(100.0%%)", totalTons));
    }
  }

  /**
   * 쓰레기양을 톤 단위로 변환합니다.
   */
  private double convertToTons(int amount) {
    // 실제 변환 로직은 프로젝트의 요구사항에 맞게 구현해야 합니다.
    return amount / 1000.0; // 예: kg을 톤으로 변환
  }

  /**
   * 쓰레기양과 퍼센티지를 문자열 형식으로 설정합니다.
   */
  private <T> void setStringField(T stat, String fieldName, double amount, double total) {
    double percentage = (total == 0) ? 0 : (amount / total) * 100;
    setFieldValue(stat, fieldName, String.format("%.2ft(%.1f%%)", amount, percentage));
  }

  /**
   * 리플렉션을 사용하여 객체의 필드 값을 가져옵니다.
   */
  private <T> double getFieldValue(T obj, String fieldName) {
    try {
      Field field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.getDouble(obj);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Error accessing field: " + fieldName, e);
    }
  }

  /**
   * 리플렉션을 사용하여 객체의 필드 값을 설정합니다.
   */
  private <T> void setFieldValue(T obj, String fieldName, Object value) {
    try {
      Field field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Error setting field: " + fieldName, e);
    }
  }
  // ------------getBasicStatistics 관련 메서드 끝-------

  // ------------getTrashDistribution 관련 메서드 시작-------
  private Clean createCleanFromDTO(CleanRequestDTO cleanRequestDTO) {

    // 필요한 researcher, beach를 찾고
    Worker cleaner = findCleaner(cleanRequestDTO.getCleanerUsername());
    Beach beach = findBeach(cleanRequestDTO.getBeachName());

    String members = listToStringMembers(cleanRequestDTO.getMembers());

    Double startLatitude = cleanRequestDTO.getStartLatitude();
    Double startLongitude = cleanRequestDTO.getStartLongitude();
    Double endLatitude = cleanRequestDTO.getEndLatitude();
    Double endLongitude = cleanRequestDTO.getEndLongitude();

    Double beachLength = DistanceCalculator.calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);

    // DTO에서 받은 값으로 Clean 생성
    Clean clean = Clean.builder()
       .cleaner(cleaner)
       .beach(beach)
        .realTrashAmount(cleanRequestDTO.getRealTrashAmount())
        .cleanDateTime(LocalDateTime.now())
        .startLatitude(startLatitude)
        .startLongitude(startLongitude)
        .endLatitude(endLatitude)
        .endLongitude(endLongitude)
        .beachLength(beachLength)
        .mainTrashType(TrashType.valueOf(cleanRequestDTO.getMainTrashType()))
        .members(members)
        .weather(cleanRequestDTO.getWeather())
        .specialNote(cleanRequestDTO.getSpecialNote())
       .build();

    // 빌더로 하기에는 까다로운 부분을 추가로 설정
    addImages(clean, cleanRequestDTO.getBeforeUploadedFileNames());
    addImages(clean, cleanRequestDTO.getAfterUploadedFileNames());

    return clean;

  }

  private void addImages(Clean clean, List<String> uploadedFileNames) {
    if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
      uploadedFileNames.forEach((fileName) -> {
        clean.addImageString(fileName);
      });
    }
  }

  private Worker findCleaner(String cleanerUsername) {
    return (Worker) memberRepository.findByUsernameWithDetails(cleanerUsername)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + cleanerUsername));
  }

  private Beach findBeach(String beachName) {
    return beachRepository.findById(beachName)
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));
  }
  // ------------getTrashDistribution 관련 메서드 끝-------

  private String listToStringMembers(List<String> members) {

    String result = "";
    if(members != null && !members.isEmpty()) {
      for (int i=0; i< members.size(); i++) {
        if ((members.size() -1) == i) {
          result += members.get(i);
        } else {
          result += members.get(i) + ",";
        }
      }
    }
    return result;
  }
}
