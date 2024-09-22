package com.boogionandon.backend.domain;


import com.boogionandon.backend.domain.enums.AvailabilityStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@DiscriminatorValue("FIELD_WORKER")
@Getter
public class FieldWorker extends Member {
  // 청소자 특정 필드

  private String workArea;  // 주요 활동 지역 (예: "부산 해운대구")
  private String equipment; // 보유 장비 (예: "쓰레기 집게, 분리수거 봉투")

  @Enumerated(EnumType.STRING)
  private AvailabilityStatus availabilityStatus; // 현재 가용 상태 (AVAILABLE, BUSY, OFFLINE 등)

  private LocalDate lastActiveDate; // 마지막 활동 날짜
  private int completedInvestigations; // 완료한 조사 횟수
  private int completedCleanups; // 완료한 청소 횟수
}