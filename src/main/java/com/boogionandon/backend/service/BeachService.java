package com.boogionandon.backend.service;

import java.util.List;
import java.util.Map;

public interface BeachService {

  // 모든 beach를 가져와 안에서 si 변환 해서 정렬 후 리스트 반환
  public List<String> SortedSiList();

  // 모든 beach를 가져와 안에서 guGun 변환해서 정렬 후 리스트 반환
  public List<String> SortedGuGunList();

  // 모든 beach를 가져와 안에서 guGun을 변환해서 정렬 후 반환
  // 시가 키값
  public Map<String, List<String>> sortedSiGuGunMap();

  // 모든 beach를 가져와 안에서 beachName을 변환해서 정렬 후 반환
  // 구군이 키값
  //  Map<String, List<String>> beachName = new HashMap<String, List<String>>();
  // 형태로 반환하기
  public Map<String, List<String>> SortedBeachNameMap();

  List<String> sortedBeachNameList();

  List<String> findSortedBeachNameListWithWorkerId(Long workerId);
}
