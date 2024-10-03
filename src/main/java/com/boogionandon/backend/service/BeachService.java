package com.boogionandon.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BeachService {

  // 모든 beach를 가져와 안에서 guGun 변환해서
  //  Set<String> guGun = new HashSet<>();
  // 형태로 반환하기
  public Set<String> guGunSet();

  // 모든 beach를 가져와 안에서 beachName을 변환해서
  //  Map<String, List<String>> beachName = new HashMap<String, List<String>>();
  // 형태로 반환하기
  public Map<String, List<String>> beachNameMap();
}
