package com.boogionandon.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdminRequestDTO {

  private String name;

  private String phone;

  private String email;

  private String address; // 구청 주소
  private String addressDetail; // 구청 상세 주소

  private String workCity; // 일하는 지역
  private String workPlace; // 구군

  private String department; // 부서

  private String position; // 직급

  private String contact; // 구청 연락처

}
