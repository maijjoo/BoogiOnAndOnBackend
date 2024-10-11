package com.boogionandon.backend.repository;

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
class AdminRepositoryTest {

  @Autowired
  private AdminRepository adminRepository;
  @Test
  @DisplayName("getAssignmentAreaList 메서드 테스트")
  void testGetAssignmentAreaList() {

    Long adminId = 6L; // initData에서 만들어진 Admin id

    List<Object[]> findData = adminRepository.getAssignmentAreaList(adminId);

    List<String> assignmentAreaList = findData.stream().map(data -> (String) data[1]).toList();

    log.info("findData.size : " + findData.size());
    log.info("findData : " + findData.toString());
    log.info("assignmentAreaList.size : " + assignmentAreaList.size());
    log.info("assignmentAreaList : " + assignmentAreaList.toString());
  }

}