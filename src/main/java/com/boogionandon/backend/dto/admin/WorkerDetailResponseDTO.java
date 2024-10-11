package com.boogionandon.backend.dto.admin;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerDetailResponseDTO {

  private Long id;
  private String name;
  private String username;
  private String phone;
  private LocalDate birth;
  private String email;
  private Double vehicleCapacity; // 차량정보(무게 ton)
  private String address; // 자기집
  private String addressDetail; // 자기집

  private String workGroup; // 소속 -> 해당 managerId의 admin의 구청 + 과/부서
  private String managerName;
  private String managerContact;

  private LocalDate startDate;
  private LocalDate endDate;

}
