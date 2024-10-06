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
  @DisplayName("findSortedWorkerNameList 테스트")
  void testFindSortedWorkerNameList() {

    List<String> list = workerService.findSortedWorkerNameList();

    log.info("list : " + list);

  }

}