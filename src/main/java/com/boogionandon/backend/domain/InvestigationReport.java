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
public class InvestigationReport extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "investigator_id", nullable = false)
  private Member investigator;

  @Column(nullable = false)
  private LocalDateTime reportTime; // 제출시 자동 입력

  // 제출시 지도 GPS를 통해 위도, 경도 자동 저장
  // 또는 여러 사진을 찍어서 올릴시 첫번째 사진을 기준으로 위도, 경도 자동 저장
  @Column(nullable = false)
  private Double latitude;  // 위도
  @Column(nullable = false)
  private Double longitude; // 경도

  @Column(nullable = false)
  private String trashDescription;

  // 다른 테이블의 작업들 (배정, 완료 등) 이 끝났으면 여기도 반영되어야 함
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status;

  @ElementCollection
  @Builder.Default
  private List<ReportImage> imageList = new ArrayList<>();

  // 추가로 필요한 것이 있다면 추가

  // 추가: 해안 길이 (m 단위)
  @Column(nullable = false)
  private Double coastlineLength;

  // 추가: 예상 쓰레기 수거량 (L 단위)
  @Column(nullable = false)
  private Double estimatedTrashVolume;

  // 추가: 주요 쓰레기 종류
  @ElementCollection
  @Enumerated(EnumType.STRING)
  private List<TrashType> mainTrashTypes = new ArrayList<>();

  public void imageListClear() {
    this.imageList.clear();
  }

  public void mainTrashTypesClear() {
    this.mainTrashTypes.clear();
  }

}
