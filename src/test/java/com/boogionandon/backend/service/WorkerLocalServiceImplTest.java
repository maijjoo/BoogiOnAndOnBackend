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

}