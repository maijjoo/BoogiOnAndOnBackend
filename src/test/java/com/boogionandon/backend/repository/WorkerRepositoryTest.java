package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Worker;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
class WorkerRepositoryTest {

  @Autowired
  private WorkerRepository workerRepository;

  @Test
  @DisplayName("getAllBySameAdmin 테스트")
  void testGetAllBySameAdmin() {
    // given
    Long adminId = 6L; // admin id

    // when
    List<Worker> allBySameAdmin = workerRepository.getAllBySameAdmin(adminId);

    // then
      log.info("allBySameAdmin : " + allBySameAdmin);
      log.info("allBySameAdmin.size() : " + allBySameAdmin.size());

  }
}