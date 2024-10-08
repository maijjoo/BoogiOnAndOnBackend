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
public class MemberInquiryPageForRegularResponseDTO {

  private Long id;

  private String name;

  private String phone;

  private Double vehicleCapacity;

  private LocalDateTime createdDate;

  private LocalDate startDate;

  private LocalDate endDate;
}
