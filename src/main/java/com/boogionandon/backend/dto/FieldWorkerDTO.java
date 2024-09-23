package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.AvailabilityStatus;
import com.boogionandon.backend.domain.enums.MemberRole;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class FieldWorkerDTO extends BaseMemberDTO{

  private String workArea;  // 주요 활동 지역 (예: "부산 해운대구")
  private String equipment; // 보유 장비 (예: "쓰레기 집게, 분리수거 봉투")

  private AvailabilityStatus availabilityStatus; // 현재 가용 상태 (AVAILABLE, BUSY, OFFLINE 등)

  private LocalDate lastActiveDate; // 마지막 활동 날짜
  private int completedInvestigations; // 완료한 조사 횟수
  private int completedCleanups; // 완료한 청소 횟수


  // 모든 필드를 포함하는 생성자 - 나중에 Entity에서 바로 저장하는 부분 추가할 예정
  public FieldWorkerDTO(Long id, String email, String password, String name, String nickname,
      String phoneNumber, MemberRole role, String workArea, String equipment,
      AvailabilityStatus availabilityStatus, LocalDate lastActiveDate,
      int completedInvestigations, int completedCleanups) {
    super(id, email, password, name, nickname, phoneNumber, role);
    this.workArea = workArea;
    this.equipment = equipment;
    this.availabilityStatus = availabilityStatus;
    this.lastActiveDate = lastActiveDate;
    this.completedInvestigations = completedInvestigations;
    this.completedCleanups = completedCleanups;
  }
}
