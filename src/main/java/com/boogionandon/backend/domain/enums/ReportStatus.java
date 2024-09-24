package com.boogionandon.backend.domain.enums;

public enum ReportStatus {
  REPORTED, // 조사자가 보고를 올렸을 때
  CLEANING_ASSIGNED, // 관리자가 청소자를 배정했을 때
  CLEANED,  // 청소자가 청소를 완료 했을 때

  COLLECTION_ASSIGNED, // 수거자가 배정되었을 때
  COLLECTED // 수거자가 수거를 완료 했을 때
}
