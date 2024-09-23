package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.MemberRole;
import lombok.Getter;

@Getter
public class AdminDTO extends BaseMemberDTO {

  private String department; // 소속 부서 (예: "환경관리과", "시스템운영팀")
  private String responsibleArea; // 담당 지역 또는 업무 영역

  private String emergencyContact; // 비상 연락처

  public AdminDTO(Long id, String email, String password, String name, String nickname,
      String phoneNumber, MemberRole role) {
    super(id, email, password, name, nickname, phoneNumber, role);
  }

  // 모든 필드를 포함하는 생성자
  public AdminDTO(Long id, String email, String password, String name, String nickname,
      String phoneNumber, MemberRole role, String department, String responsibleArea,
      String emergencyContact) {
    super(id, email, password, name, nickname, phoneNumber, role);
    this.department = department;
    this.responsibleArea = responsibleArea;
    this.emergencyContact = emergencyContact;
  }
}
