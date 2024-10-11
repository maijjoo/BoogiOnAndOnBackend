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
public class CreateWorkerRequestDTO {

  // username, password는 랜덤으로 생성

  private String name;

  private String phone;

  private LocalDate birth;

  private String email;

  private Double vehicleCapacity;

  private String address;

  private String addressDetail;

  private LocalDate startDate;

  private LocalDate endDate;

}
