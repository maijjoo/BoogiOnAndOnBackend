package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("WORKER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Worker extends Member{

  @Column(nullable = false)
  private LocalDate birth;

  @Column
  private Double vehicleCapacity; // 차량정보(무게 ton)

  // 추가로 필요한 필드가 있다면 추가

  @Column(nullable = false)
  private LocalDate startDate; // 관리자가 등록해주는 할 수 있는 일자 (시작)

  @Column(nullable = false)
  private LocalDate endDate; // 관리자가 등록해주는 할 수 있는 일자 (종료)

  public void updateVehicleCapacity(Double vehicleCapacity) {
    this.vehicleCapacity = vehicleCapacity;
  }
}
