package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class AdminServiceTest {

  @Autowired
  private AdminService adminService;

  @Test
  @DisplayName("getAssignmentAreaList 테스트")
  void getAssignmentAreaList() {

    Long adminId = 6L;

    List<String> find = adminService.getAssignmentAreaList(adminId);

    log.info("find : " + find);
  }

}