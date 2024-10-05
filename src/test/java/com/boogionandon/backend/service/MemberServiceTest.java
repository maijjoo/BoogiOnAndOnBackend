package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class MemberServiceTest {

  @Autowired
  private MemberService memberService;

  @Test
  @DisplayName("getWorkerProfile 테스트")
  void testGetWorkerProfile() {

    Long workerId = 8L; // initData 에 들어가 있는 Worker -> 8L, 9L, 10L, 11L;

    WorkerResponseDTO workerProfile = memberService.getWorkerProfile(workerId);

    log.info("workerProfile : " + workerProfile);

  }

  @Test
  @DisplayName("getAdminProfile 테스트")
  void testGetAdminProfile() {

    Long adminId = 5L; // initData 에 들어가 있는 Worker -> 8L, 9L, 10L, 11L;

    AdminResponseDTO adminProfile = memberService.getAdminProfile(adminId);

    log.info("adminProfile : " + adminProfile);

  }

}