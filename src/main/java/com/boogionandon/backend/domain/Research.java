package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Research extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "researcher_id", nullable = false)
  private RegularMember researcher; // 조사자

  @Column(length = 20, nullable = false)
  private String beachName;

  // 추가: 해안 길이 (m 단위)
  @Column(nullable = false)
  private Double beachLength; // ex) 19.2m

  // 추가: 예상 쓰레기 수거량 (L 단위)
  @Column(nullable = false)
  private Integer expectedTrashAmount; // ex) 50L

  @Column(nullable = false)
  private LocalDateTime reportTime; // 제출시 자동 입력

  // 제출시 지도 GPS를 통해 위도, 경도 자동 저장
  // 또는 여러 사진을 찍어서 올릴시 첫번째 사진을 기준으로 위도, 경도 자동 저장
  // 기준을 잡아야 할듯 첫번째 사진으로 위,경도를 저장할지, GPS로 폼을 올릴때 위치를 저장할지
  // 다른 곳에서도 공통적으로 쓰기 위해
  @Column(nullable = false)
  private Double latitude;  // 위도
  @Column(nullable = false)
  private Double longitude; // 경도

  // 현재는 한개 받는거로
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrashType mainTrashType; // 주요 쓰레기 타입

  // 다른 테이블의 작업들 (배정, 완료 등) 이 끝났으면 여기도 반영되어야 함
  //  REPORTED, // 조사자가 보고를 올렸을 때
  //  CLEANING_ASSIGNED, // 관리자가 청소자를 배정했을 때
  //  CLEANED,  // 청소자가 청소를 완료 했을 때
  //  COLLECTION_ASSIGNED, // 수거자가 배정되었을 때 - Research 에서는 안쓸 수 있음
  //  COLLECTED // 수거자가 수거를 완료 했을 때 - Research 에서는 안쓸 수 있음
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status;


  // 이것도 청소 리포트 이미지들과 같은 공간에서 처리해야 할듯
  // 조사 사진은 R_202409241052.... 같이 생각 (최소 5장 ~ 최대 20장)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 조사된 이미지들

  // 아래부터는 관리자가 배정되고 청소자 배정하는...
  // 만들어질때는 null이었다가 추후 배정되는 걸로

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private Admin admin;  // 관리자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cleaner_id")
  private RegularMember cleaner; // 청소자

  // 추가로 필요한 것이 있다면 추가

  private LocalDateTime cleanerAssignedTime; // 필요하지 않을까?



}

// Assignment 를 지우고 통합???
// 처음 리포트를 받았을때는 배정자가 비어있지만 관리자가 나중에 추가해주는 식으로