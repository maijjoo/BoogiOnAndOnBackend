package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.CollectorStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@DiscriminatorValue("COLLECTOR")
@Getter
public class Collector extends Member {
  // 수거자 특정 필드

  private String vehicleType; // 수거 차량 유형 (예: "소형 트럭", "대형 수거차")
  private String vehicleLicensePlate; // 차량 번호판
  private double vehicleCapacity; // 차량 적재 용량 (단위: kg)

  private String collectionArea; // 주요 수거 지역

  private int completedCollections; // 완료한 수거 작업 횟수
  private double totalCollectedAmount; // 총 수거한 쓰레기양 (단위: kg)

  @Enumerated(EnumType.STRING)
  private CollectorStatus status; // 현재 상태 (AVAILABLE, ON_ROUTE, COLLECTING, OFFLINE 등)

  private LocalDateTime lastCollectionTime; // 마지막 수거 작업 시간

}
