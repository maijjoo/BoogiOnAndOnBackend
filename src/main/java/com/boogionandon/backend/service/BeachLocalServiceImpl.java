package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.repository.BeachRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BeachLocalServiceImpl implements BeachService{

  private final BeachRepository beachRepository;

  @Override
  public Set<String> guGunSet() {

    List<Beach> all = beachRepository.findAll();
    Set<String> guGun = new HashSet<>();

    all.stream().forEach(beach -> {
      // Set이어서 중복은 저장 안함
      guGun.add(beach.getGuGun());
    });

    return guGun;
  }

  @Override
  public Map<String, List<String>> beachNameMap() {

    List<Beach> all = beachRepository.findAll();
    Map<String, List<String>> beachName = new HashMap<>();

    all.stream().forEach(beach -> {
      // guGun과 beachName을 Map<guGun, List<beachName>> 에 넣어서 return

      //먼저 beach.getGuGun()에 해당하는 키(구/군)가 이미 맵에 존재하는지 확인합니다.
      //만약 존재하지 않으면, 새로운 ArrayList(비어 있는 리스트)를 생성하고 이를 해당 키에 매핑합니다.
      //키가 존재할 경우, 기존에 매핑된 리스트를 반환합니다.
      //add(beach.getBeachName()):
      //
      //반환된 리스트에 새로운 해변 이름을 추가합니다.
      beachName.computeIfAbsent(beach.getGuGun(), k -> new ArrayList<>()).add(beach.getBeachName()); // 이 코드는 이해가 안감

    });

    return beachName;
  }
}
