package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.admin.CreateWorkerRequestDTO;
import java.time.LocalDate;
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
class WorkerLocalServiceImplTest {

  @Autowired
  private WorkerService workerService;

  @Test
  @DisplayName("findSortedWorkerNameListWithWorkerId 테스트")
  void findSortedWorkerNameListWithWorkerId() {

    Long workerId = 12L; // worker id

    List<String> list = workerService.findSortedWorkerNameListWithWorkerId(workerId);

    log.info("list : " + list);
    log.info("list size : " + list.size());

  }

  // ------------ Worker, Admin 단일/다중 테스트 시작 ------------------
  @Test
  @DisplayName("createOneWorker 테스트")
  @Commit
  void testCreateOneWorkerTest() {

    Long adminId = 5L; // 관리하는 어드민의 id (로그인해서 Worker를 만드는)

    CreateWorkerRequestDTO workerRequest = CreateWorkerRequestDTO.builder()
        .name("라주")
        .phone("010-3256-1212")
        .birth(LocalDate.of(1960,02,01))
        .email("lisa12431@test.com")
        .vehicleCapacity(1.5)
        .address("부산 광역시 남구")
        .addressDetail("용호로 216번가길 10")
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusMonths(6))
        .build();

    workerService.createOneWorker(adminId, workerRequest);

    log.info("worker created successfully");
  }

  // ------------ Worker, Admin 단일/다중 테스트 끝 ------------------

}