package com.boogionandon.backend.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInquiryPageForSuperResponseDTO {

  // 공통으로 쓰는 필드
  private Long id;
  private String name;
  private String role; // 구분하기 위한 필드

  // 관리자 리스트에서 보여줘야 하는 필드
  private String workPlace; // 근무처
  private String department;  // 부서
  private String contact; // 근무처 연락처
  private String email;

  // Worker (조사/청소, 수거자) 일때 보여줘야 하는 필드
  private String phone;
  private Double vehicleCapacity; // 차량 적재량
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDateTime createdDate;


}
