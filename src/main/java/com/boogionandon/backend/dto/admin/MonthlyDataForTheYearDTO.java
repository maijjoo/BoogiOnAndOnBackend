package com.boogionandon.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 특정 연도 내 월별 쓰레기 통계 데이터를 표현하는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyDataForTheYearDTO {
  /** 통계 대상 월 (1-12) */
  private int month;

  /** 해당 월에 조사한 지역의 수 */
  private int surveyAreaCount;

  /** 부표류 쓰레기 양과 비율 (예: "10.5t(10.0%)") */
  private String buoyDebris;

  /** 생활쓰레기류 양과 비율 */
  private String householdWaste;

  /** 대형 투기쓰레기류 양과 비율 */
  private String largeDisposalWaste;

  /** 초목류 쓰레기 양과 비율 */
  private String vegetationWaste;

  /** 폐어구류 쓰레기 양과 비율 */
  private String fishingGearWaste;

  /** 총 쓰레기 양과 비율 (항상 "X.Xt(100.0%)") */
  private String total;

  /** 부표류 쓰레기 양 (톤 단위) */
  private double buoyDebrisTons;

  /** 생활쓰레기류 양 (톤 단위) */
  private double householdWasteTons;

  /** 대형 투기쓰레기류 양 (톤 단위) */
  private double largeDisposalWasteTons;

  /** 초목류 쓰레기 양 (톤 단위) */
  private double vegetationWasteTons;

  /** 폐어구류 쓰레기 양 (톤 단위) */
  private double fishingGearWasteTons;

  /** 총 쓰레기 양 (톤 단위) */
  private double totalTons;
}
