package com.boogionandon.backend.domain.enums;

public enum CollectorStatus {
  AVAILABLE, // 수거자가 수거가능 할때 // 이게 필요할 까? 시작하면 ON_ROUTE에 포함되는 거 아닐까?
  ON_ROUTE, // 수거하러 돌고 있을 때
  COLLECTING, // 수거 할때 // 이게 언제 바뀌어야 되는거지? 완료 폼을 누르는 것 밖에 없는데 그럼 순식간에 ON_ROUTE로 바뀌는 거 아닌가?
  OFFLINE
}
