package com.boogionandon.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDetailResponseDTO {

  private Long id;
  private String name;
  private String username;
  private String phone;
  private String contact;
  private String email;
  private String address; // 자기집
  private String addressDetail; // 자기집

  private String workCity;
  private String workGroup; // 소속 -> admin의 구청 + 과/부서

}
