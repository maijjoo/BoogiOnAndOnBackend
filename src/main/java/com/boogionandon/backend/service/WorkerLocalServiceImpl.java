package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.dto.admin.CreateWorkerRequestDTO;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.WorkerRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class WorkerLocalServiceImpl implements WorkerService{

  private final WorkerRepository workerRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final Map<String, AtomicInteger> dailyCounters = new ConcurrentHashMap<>();


  @Override
  public List<String> findSortedWorkerNameListWithWorkerId(Long workerId) {

    Object[] byIdWithManager = memberRepository.findByIdWithManager(workerId)
        .orElseThrow(() -> new RuntimeException("Member not found with WorkerId : " + workerId));

    Long adminId = null;
    if (byIdWithManager.length > 0 && byIdWithManager[0] instanceof Object[]) {
      Object[] innerArray = (Object[]) byIdWithManager[0];
      if (innerArray.length >= 2 && innerArray[1] instanceof Admin) {
        adminId = ((Admin) innerArray[1]).getId();
      } else {
        log.error("Admin not found with WorkerId : " + workerId);
      }
    }else {
      // WorkerId로 Member 찾기 실패
      log.error("findSortedBeachNameListWithWorkerId - Member not found with WorkerId : " + workerId);
      return new ArrayList<>();
    }

    List<Worker> allBySameAdmin = workerRepository.getAllBySameAdmin(adminId);

    List<String> nameWithLastFourNumber = allBySameAdmin.stream()
        .map(worker -> {
          String name = worker.getName();
          String phone = worker.getPhone();
          if (phone != null) {
            // '-'를 제거하고 숫자만 남김
            String digitsOnly = phone.replaceAll("-", "");
            if (digitsOnly.length() >= 4) {
              return name + " " + digitsOnly.substring(digitsOnly.length() - 4);
            }
          }
          return name + " (전화번호 없음)";
        })
        .collect(Collectors.toList());

    nameWithLastFourNumber.sort(String::compareTo);  // String::compareTo : compareTo()를 구현한 String 클래스를 사용

    log.info("nameWithLastFourNumber " + nameWithLastFourNumber);
    log.info("nameWithLastFourNumber.size() :  " + nameWithLastFourNumber.size());



    return nameWithLastFourNumber;
  }

  @Override
  public void createOneWorker(Long adminId, CreateWorkerRequestDTO createWorkerRequestDTO) {

    String username = createRandomUsername();

    Worker worker = Worker.builder()
        .username(username)
        .password(passwordEncoder.encode("0000"))
        .email(createWorkerRequestDTO.getEmail())
        .name(createWorkerRequestDTO.getName())
        .phone(createWorkerRequestDTO.getPhone())
        .address(createWorkerRequestDTO.getAddress())
        .addressDetail(createWorkerRequestDTO.getAddressDetail())
        .birth(createWorkerRequestDTO.getBirth())
        .vehicleCapacity(createWorkerRequestDTO.getVehicleCapacity())
        .startDate(createWorkerRequestDTO.getStartDate())
        .endDate(createWorkerRequestDTO.getEndDate())
        .managerId(adminId)
        .build();

    worker.getMemberRoleList().add(MemberType.WORKER);

    log.info("worker : " + worker);
    workerRepository.save(worker);
  }

  @Override
  public List<CreateWorkerRequestDTO> exelToDTOList(MultipartFile exel) throws IOException {
    List<CreateWorkerRequestDTO> workers = new ArrayList<>();

    // Workbook은 POI 디펜던시 안에 있는 것, 내가 만든거 아님
    try (InputStream is = exel.getInputStream()) {
      Workbook workbook;
      if (exel.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
        workbook = new XSSFWorkbook(is);
      } else if (exel.getOriginalFilename().toLowerCase().endsWith(".xls")) {
        workbook = new HSSFWorkbook(is);
      } else {
        throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. .xlsx 또는 .xls 파일만 허용됩니다.");
      }

      Sheet sheet = workbook.getSheetAt(0); // 0번째 Sheet만 사용

      for (Row row : sheet) {
        if (row.getRowNum() == 0) { // 헤더 부분은 넘기기
          continue;
        }

        try {
          CreateWorkerRequestDTO worker = CreateWorkerRequestDTO.builder()
              .name(getCellValueAsString(row.getCell(0)))
              .phone(getCellValueAsString(row.getCell(1)))
              .birth(LocalDate.parse(getCellValueAsString(row.getCell(2))))
              .email(getCellValueAsString(row.getCell(3)))
              .vehicleCapacity(Double.parseDouble(getCellValueAsString(row.getCell(4))))
              .address(getCellValueAsString(row.getCell(5)))
              .addressDetail(getCellValueAsString(row.getCell(6)))
              .startDate(LocalDate.parse(getCellValueAsString(row.getCell(7))))
              .endDate(getCellValueAsString(row.getCell(8)).isEmpty() ? null : LocalDate.parse(getCellValueAsString(row.getCell(8))))
              .build();

          log.info("worker : " + worker);
          workers.add(worker);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("Row " + (row.getRowNum() + 1) + " 처리 중 오류: " + e.getMessage());
        }
      }
    }
    return workers;
  }

  private String createRandomUsername() {
    LocalDate now = LocalDate.now();
    String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    String key = "W_" + datePart;
    AtomicInteger counter = dailyCounters.computeIfAbsent(key, k -> new AtomicInteger(0));
    int sequenceNumber = counter.incrementAndGet();

    return String.format("%s%03d", key, sequenceNumber);
  }

  // 매일 자정에 실행되어야 하는 메소드
  @Scheduled(cron = "0 0 0 * * ?")
  public void resetDailyCounters() {
    dailyCounters.clear();
  }

  private String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getLocalDateTimeCellValue().toLocalDate().toString();
        }
        return String.valueOf(cell.getNumericCellValue());
      default:
        return "";
    }
  }
}
