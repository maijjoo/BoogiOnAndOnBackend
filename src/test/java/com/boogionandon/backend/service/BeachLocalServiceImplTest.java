package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class BeachLocalServiceImplTest {

  @Autowired
  private BeachService beachService;

  @Test
  @DisplayName("SortedSiList 테스트")
  void SortedSiList() {

    List<String> siList = beachService.SortedSiList();

    log.info("siList : " + siList);
  }

  @Test
  @DisplayName("SortedGuGunList 테스트")
  void SortedGuGunList() {

    List<String> guGunList = beachService.SortedGuGunList();

    log.info("guGunList : " + guGunList);

  }

  @Test
  @DisplayName("sortedSiGuGunMap 테스트")
  void sortedSiGuGunMap() {

    Map<String, List<String>> sortedSiGuGunMap = beachService.sortedSiGuGunMap();

    log.info("sortedSiGuGunMap : " + sortedSiGuGunMap);
  }

  @Test
  @DisplayName("SortedBeachNameMap 테스트")
  void beachName() {

    Map<String, List<String>> beachName = beachService.SortedBeachNameMap();

    log.info("beachName : " + beachName);
  }

  @Test
  @DisplayName("sortedBeachNameList 테스트")
  void testSortedBeachNameList() {

    List<String> sortedBeachNameList = beachService.sortedBeachNameList();

    log.info("sortedBeachNameList : " + sortedBeachNameList);
  }
}