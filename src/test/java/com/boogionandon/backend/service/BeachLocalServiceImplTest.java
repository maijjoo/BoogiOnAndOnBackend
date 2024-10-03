package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
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
  void guGun() {

    Set<String> guGun = beachService.guGunSet();

    log.info("guGun : " + guGun);

  }

  @Test
  void beachName() {

    Map<String, List<String>> beachName = beachService.beachNameMap();

    log.info("beachName : " + beachName);
  }
}