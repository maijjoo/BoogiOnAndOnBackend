package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("WORKER")
public class Worker extends Member{

  @Column(length = 20, nullable = false)
  private String contact; // 근무처 연락처

  @Column(length = 100)
  private String workGroup; // 소속

  @Column(length = 100)
  private String workAddress; //// 소속 주소

  @Column(length = 150)
  private String workAddressDetail; // 소속 상세 주소

  private int vehicleCapacity; // 차량정보(무게 ton)





  // 추가로 필요한 필드가 있다면 추가

}
