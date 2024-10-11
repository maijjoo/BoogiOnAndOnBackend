package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.dto.admin.CreateAdminRequestDTO;
import com.boogionandon.backend.repository.AdminRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AdminLocalServiceImpl implements AdminService{

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
}
