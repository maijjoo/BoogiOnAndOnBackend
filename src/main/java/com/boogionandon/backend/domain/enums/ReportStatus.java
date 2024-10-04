package com.boogionandon.backend.domain.enums;

public enum ReportStatus {
  ASSIGNMENT_NEEDED,  // 배정이 필요한 단계 - 화면에 보일예정
  ASSIGNMENT_COMPLETED, // 배정이 완료된 단계 - 화면에 안보일 예정

  ASSIGNMENT_ADDED_TO_ROUTE // 수거자 화면에서 경로추가 눌렀을 때 바뀜, 수거 완료 누르면 ASSIGNMENT_COMPLETED로 바뀔거임
}
