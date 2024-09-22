package com.boogionandon.backend.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends Member{
  // 수거자 특정 필드

  private String department; // 소속 부서 (예: "환경관리과", "시스템운영팀")
  private String responsibleArea; // 담당 지역 또는 업무 영역

  private String emergencyContact; // 비상 연락처

}
