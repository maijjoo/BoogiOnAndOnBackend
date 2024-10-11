package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.MemberRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BeachLocalServiceImpl implements BeachService{

  private final BeachRepository beachRepository;
  private final MemberRepository memberRepository;

  @Override
  public List<String> SortedSiList() {

    // 1. 모든 해변 정보를 가져옵니다.
    List<Beach> allBeaches = beachRepository.findAll();

    // 2. 결과를 저장할 TreeSet을 생성합니다. (자동 정렬 및 중복 제거)
    Set<String> uniqueSortedSi = new TreeSet<>();

    // 3. 각 해변에서 구/군 정보를 추출하여 Set에 추가합니다.
    for (Beach beach : allBeaches) {
      String si = beach.getSi();
      uniqueSortedSi.add(si);
    }
    log.info("uniqueSortedSi : " + uniqueSortedSi);
    return uniqueSortedSi.stream().toList();

  }

  // 정렬 되어 있음
  @Override
  public List<String> SortedGuGunList() {

    // 1. 모든 해변 정보를 가져옵니다.
    List<Beach> allBeaches = beachRepository.findAll();

    // 2. 결과를 저장할 TreeSet을 생성합니다. (자동 정렬 및 중복 제거)
    Set<String> uniqueSortedGuGuns = new TreeSet<>();

    // 3. 각 해변에서 구/군 정보를 추출하여 Set에 추가합니다.
    for (Beach beach : allBeaches) {
      String guGun = beach.getGuGun();
      uniqueSortedGuGuns.add(guGun);
    }
    log.info("uniqueSortedGuGuns : " + uniqueSortedGuGuns);
    return uniqueSortedGuGuns.stream().toList();
  }

  // super관리자가 admin 만들때 사용 예정
  @Override
  public Map<String, List<String>> sortedSiGuGunMap() {
    List<Beach> allBeaches = beachRepository.findAll();
    Map<String, List<String>> siGuGunMap = new TreeMap<>();

    // 해변 정보를 맵에 추가
    for (Beach beach : allBeaches) {
      String si = beach.getSi();  // 시 정보를 가져오는 메소드가 필요합니다
      String guGun = beach.getGuGun();

      // 시에 해당하는 리스트가 없으면 새로 생성
      if (!siGuGunMap.containsKey(si)) {
        siGuGunMap.put(si, new ArrayList<>());
      }

      // 구/군이 리스트에 없으면 추가
      if (!siGuGunMap.get(si).contains(guGun)) {
        siGuGunMap.get(si).add(guGun);
      }
    }

    // 각 시의 구/군 리스트 정렬
    for (List<String> guGuns : siGuGunMap.values()) {
      Collections.sort(guGuns);
    }

    return siGuGunMap;
  }

  // 정렬 되어 있음
  @Override
  public Map<String, List<String>> SortedBeachNameMap() {

    List<Beach> all = beachRepository.findAll();
    Map<String, List<String>> beachNameMap = new TreeMap<>();

    // 해변 정보를 맵에 추가
    for (Beach beach : all) {
      String guGun = beach.getGuGun();
      String beachName = beach.getBeachName();

      // 구/군에 해당하는 리스트가 없으면 새로 생성
      if (!beachNameMap.containsKey(guGun)) {
        beachNameMap.put(guGun, new ArrayList<>());
      }

      // 해변 이름을 리스트에 추가
      beachNameMap.get(guGun).add(beachName);
    }

    // 각 구/군의 해변 리스트 정렬
    for (List<String> beaches : beachNameMap.values()) {
      Collections.sort(beaches);
    }

    return beachNameMap;
  }

  @Override
  public List<String> sortedBeachNameList() {

    // 1. 모든 해변 정보를 가져옵니다.
    List<Beach> allBeaches = beachRepository.findAll();

    // 2. 결과를 저장할 TreeSet을 생성합니다. (자동 정렬 및 중복 제거)
    Set<String> uniqueSortedBeachName = new TreeSet<>();

    // 3. 각 해변에서 구/군 정보를 추출하여 Set에 추가합니다.
    for (Beach beach : allBeaches) {
      String beachName = beach.getBeachName();
      uniqueSortedBeachName.add(beachName);
    }
    log.info("uniqueSortedBeachName : " + uniqueSortedBeachName);
    return uniqueSortedBeachName.stream().toList();
  }

  @Override
  public List<String> findSortedBeachNameListWithWorkerId(Long workerId) {

    Object[] byIdWithManager = memberRepository.findByIdWithManager(workerId)
        .orElseThrow(() -> new RuntimeException("Member not found with WorkerId : " + workerId));


    List<String> beachNameList = null;
    if (byIdWithManager.length > 0 && byIdWithManager[0] instanceof Object[]) {
      Object[] innerArray = (Object[]) byIdWithManager[0];

      if (innerArray.length >= 2 && innerArray[1] instanceof Admin) {
        Admin admin = (Admin) innerArray[1];
        beachNameList = admin.getAssignmentAreaList();
      } else {
        log.error("Admin not found with WorkerId : " + workerId);
      }
    } else {
      // WorkerId로 Member 찾기 실패
      log.error("findSortedBeachNameListWithWorkerId - Member not found with WorkerId : " + workerId);
      return new ArrayList<>();
    }
    return beachNameList;
  }
}
