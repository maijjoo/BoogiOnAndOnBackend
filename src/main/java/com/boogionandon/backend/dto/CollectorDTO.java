package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.CollectorStatus;
import com.boogionandon.backend.domain.enums.MemberRole;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CollectorDTO extends BaseMemberDTO{

  private String vehicleType; // 수거 차량 유형 (예: "소형 트럭", "대형 수거차")
  private String vehicleLicensePlate; // 차량 번호판
  private double vehicleCapacity; // 차량 적재 용량 (단위: kg)

  private String collectionArea; // 주요 수거 지역

  private int completedCollections; // 완료한 수거 작업 횟수
  private double totalCollectedAmount; // 총 수거한 쓰레기양 (단위: kg)

  private CollectorStatus status; // 현재 상태 (AVAILABLE, ON_ROUTE, COLLECTING, OFFLINE 등)

  private LocalDateTime lastCollectionTime; // 마지막 수거 작업 시간

  // 모든 필드를 포함하는 생성자
  public CollectorDTO(Long id, String email, String password, String name, String nickname,
      String phoneNumber, MemberRole role, String vehicleType, String vehicleLicensePlate,
      double vehicleCapacity, String collectionArea, int completedCollections,
      double totalCollectedAmount, CollectorStatus status, LocalDateTime lastCollectionTime) {
    super(id, email, password, name, nickname, phoneNumber, role);
    this.vehicleType = vehicleType;
    this.vehicleLicensePlate = vehicleLicensePlate;
    this.vehicleCapacity = vehicleCapacity;
    this.collectionArea = collectionArea;
    this.completedCollections = completedCollections;
    this.totalCollectedAmount = totalCollectedAmount;
    this.status = status;
    this.lastCollectionTime = lastCollectionTime;
  }

}
