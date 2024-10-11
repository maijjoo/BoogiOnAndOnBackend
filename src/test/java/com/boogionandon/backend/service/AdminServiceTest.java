package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.dto.admin.CreateAdminRequestDTO;
import com.boogionandon.backend.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class AdminServiceTest {

  @Autowired
  private AdminService adminService;
  @Autowired
  private AdminRepository adminRepository;

  @Test
  @DisplayName("getAssignmentAreaList 테스트")
  void getAssignmentAreaList() {

    Long adminId = 6L;

    List<String> find = adminService.getAssignmentAreaList(adminId);

    log.info("find : " + find);
  }

  @Test
  @DisplayName("createOneAdmin 테스트")
  @Commit
  void testCreateOneAdmin() {
    Long adminId = 1L; // super admin id

    Admin admin = adminRepository.findById(adminId)
        .orElseThrow(() -> new EntityNotFoundException("해당 관리자를 찾을 수 없습니다. : " + adminId));

    // 여기 부터
    CreateAdminRequestDTO createAdminRequestDTO = CreateAdminRequestDTO.builder()
        .name("송지")
        .phone("010-1254-4512")
        .email("te123st@te32st.com")
        .address("부산 광역시 수영구")
        .addressDetail("광안 아파트 1102호")
        .workCity(admin.getWorkCity())
        .workPlace("수영구")
        .department("해양 수산과")
        .position("부장")
        .contact("051-3131-1515")
        .build();

    adminService.createOneAdmin(adminId, createAdminRequestDTO);
  }

}