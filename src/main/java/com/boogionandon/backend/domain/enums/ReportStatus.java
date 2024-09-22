package com.boogionandon.backend.domain.enums;

public enum ReportStatus {
  // 수거자가 사용한 폼에서 사용
  // 계속 추적되는 느낌

  REPORTED, // 조사자가 보고를 올렸을 때
  CLEANING_ASSIGNED, // 관리자가 청소자를 배정했을 때
  CLEANED,  // 청소자가 청소를 완료 했을 때

  // 만약 CollectionPoint 엔티티를 사용할경우 아래 내용은 필요 없을 듯
  COLLECTION_ASSIGNED, // 관리자가 수거자를 배정했을 때
  COLLECTED // 수거자가 수거를 완료 했을 때
}
