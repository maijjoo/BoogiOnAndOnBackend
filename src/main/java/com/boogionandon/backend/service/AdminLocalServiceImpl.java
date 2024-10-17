package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.dto.admin.CreateAdminRequestDTO;
import com.boogionandon.backend.repository.AdminRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AdminLocalServiceImpl implements AdminService {

  private final AdminRepository adminRepository;
  private final BeachService beachService;
  private final PasswordEncoder passwordEncoder;
  private final Map<String, AtomicInteger> dailyCounters = new ConcurrentHashMap<>();


  @Override
  public List<String> getAssignmentAreaList(Long adminId) {
    List<Object[]> findData = adminRepository.getAssignmentAreaList(adminId);

    return findData.stream().map(data -> (String) data[1]).toList();
  }

  @Override
  public void createOneAdmin(Long adminId, CreateAdminRequestDTO createAdminRequestDTO) {
    String username = createRandomUsername();

    Admin admin = Admin.builder()
        .username(username)
        .password(passwordEncoder.encode("0000"))
        .email(createAdminRequestDTO.getEmail())
        .name(createAdminRequestDTO.getName())
        .phone(createAdminRequestDTO.getPhone())
        .address(createAdminRequestDTO.getAddress())
        .addressDetail(createAdminRequestDTO.getAddressDetail())
        .workCity(createAdminRequestDTO.getWorkCity())
        .workPlace(createAdminRequestDTO.getWorkPlace())
        .department(createAdminRequestDTO.getDepartment())
        .position(createAdminRequestDTO.getPosition())
        .contact(createAdminRequestDTO.getContact())
        .managerId(adminId)
        .build();

    admin.getMemberRoleList().add(MemberType.ADMIN);

    Map<String, List<String>> beachNameMap = beachService.SortedBeachNameMap();
    log.info("beachNameMap : " + beachNameMap);
    log.info("admin.workPlace : " + admin.getWorkPlace());
    List<String> assignmentAreaList = beachNameMap.get(admin.getWorkPlace());
    log.info("assignmentAreaList : " + assignmentAreaList);
    for (String area : assignmentAreaList) {
      admin.getAssignmentAreaList().add(area);
    }

    log.info("admin : " + admin);
    adminRepository.save(admin);

  }

  @Override
  public List<CreateAdminRequestDTO> exelToDTOList(MultipartFile exel) throws IOException {
    List<CreateAdminRequestDTO> admins = new ArrayList<>();

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
          CreateAdminRequestDTO admin = CreateAdminRequestDTO.builder()
              .name(getCellValueAsString(row.getCell(0)))
              .phone(getCellValueAsString(row.getCell(1)))
              .email(getCellValueAsString(row.getCell(2)))
              .address(getCellValueAsString(row.getCell(3)))
              .addressDetail(getCellValueAsString(row.getCell(4)))
              .workCity(getCellValueAsString(row.getCell(5)))
              .workPlace(getCellValueAsString(row.getCell(6)))
              .department(getCellValueAsString(row.getCell(7)))
              .position(getCellValueAsString(row.getCell(8)))
              .contact(getCellValueAsString(row.getCell(9)))
              .build();

          log.info("admin : " + admin);
          admins.add(admin);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("Row " + (row.getRowNum() + 1) + " 처리 중 오류: " + e.getMessage());
        }
      }
    }
    return admins;
  }

  private String createRandomUsername() {
    LocalDate now = LocalDate.now();
    String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    String key = "A_" + datePart;
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
