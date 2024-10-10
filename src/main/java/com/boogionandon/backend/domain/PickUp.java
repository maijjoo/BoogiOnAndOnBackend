package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"images"})
public class PickUp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submitter_id", nullable = false)
  private Worker submitter; // 청소자, 청소를 하고 이 보고서를 올린 로그인 자

  @Column(nullable = false, length = 30)
  private String pickUpPlace;

  // 위도,경도는 자동 사진 찍을때? 폼을 전송할때?
  // 여기는 집하장소가 1개일 것이기 때문에 아래처럼 해도 될듯
  @Column(nullable = false)
  private Double latitude;  // 집하장소 위치 위도
  @Column(nullable = false)
  private Double longitude; // 집하장소 위치 경도

  // TODO : 주요 쓰레기는 일단 단일로 잡았는데 협의 필요
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrashType mainTrashType; // 주요 쓰레기 타입

  // 집하장소에 대한 정보를 올린 시간
  @Column
  private LocalDateTime submitDateTime; // 서비스에서 자동으로 넣을 예정

  // TODO : 이걸 청소자가 작성할지, 수거자가 작성할지 아니면 각각 다 입력할지
  @Column(nullable = false)
  private Integer realTrashAmount;

  // 집하장소 사진은 C_202409251220.... 같이 생각 (3장 이하)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 집하장소 사진

  //  ASSIGNMENT_NEEDED,  // 배정이 필요한 단계 - 화면에 보일예정
  //  ASSIGNMENT_COMPLETED // 배정이 완료된 단계 - 화면에 안보일 예정??
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private ReportStatus status = ReportStatus.ASSIGNMENT_NEEDED;

  public void addImageString(String fileName) {
    Image image = Image.builder()
            .fileName(fileName)
            .build();

    addImage(image);
  }

  private void addImage(Image image) {
    image.setOrd(images.size());
    images.add(image);
  }

  public void statusToCompleted() {
    if (status == ReportStatus.ASSIGNMENT_NEEDED) {
      status = ReportStatus.ASSIGNMENT_COMPLETED;
    } else {
      throw new IllegalStateException("Status is not ASSIGNMENT_NEEDED");
    }
  }

  public void changeStatusToAddedToRoute(ReportStatus reportStatus) {
    this.status = reportStatus;
  }

  public void changeStatusToCompleted(ReportStatus reportStatus) {
    this.status = reportStatus;
  }

  public void changeStatusFromAddedToNeeded(ReportStatus reportStatus) {
    this.status = reportStatus;
  }
}
