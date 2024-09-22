package com.boogionandon.backend.domain.enums;

public enum CollectionPointStatus {
  REGISTERED, // 등록됨
  WAITING_FOR_COLLECTION, //수거 대기 중 // 필요한가?
  IN_PROGRESS, // 수거 진행 중 // 필요한가?
  COLLECTED, // 수거 완료
  VERIFIED, // 확인 완료 // 필요한가?
  CLOSED // 종료
}
