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
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"images"})
public class Clean extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  // 그런데 보통 단체로 가서 하지 않나??
  // 이건 대표자라 하고 참가한 사람들의 이름을 넣어주는 List가 있어야 할까?
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cleaner_id", nullable = false)
  private Worker cleaner; // 청소자, 청소를 하고 이 보고서를 올린 로그인 자


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "beach_id", nullable = false)
  private Beach beach; // 해안

  @Column(nullable = false)
  private Integer realTrashAmount; // 실제 쓰레기 양 (ex - 50L쓰레기 봉투를 기준으로 갯수로 계산 예정)

  // 청소일시는 자동? 수동?
  // 자동이면 사진을 찍을때? 폼을 전송할때?
  @Column(nullable = false)
  private LocalDateTime cleanDateTime;

  // 위도,경도는 자동 사진 찍을때? 폼을 전송할때?
  // 사진으로 하는 경우 여러개를 받아서 평균위도를 구해야 하니 List<Double> 같이 만들어야 할듯
  // 그런데 만약 다른 사람이 찍은 사진을 받아서 사용한다고 하면 위, 경도 정보가 안들어 있을 수 있음
  // TODO : 결정내고 바꾸기
  @Column(nullable = false)
  private Double startLatitude;  //  청소 시작 위치 위도
  @Column(nullable = false)
  private Double startLongitude; // 청소 시작 위치 경도

  @Column(nullable = false)
  private Double endLatitude;  //  청소 끝 위치 위도
  @Column(nullable = false)
  private Double endLongitude; // 청소 끝 위치 경도

  // 추가: 해안 길이 (m 단위)
  // service 엔티티로 바꿀때 에서 위경도 값을 계산해서 넣기
  @Column(nullable = false)
  private Double beachLength; // ex) 19.2m

  // 주요 쓰레기는 조사내용과 일치하는지 파악하기 위해 필요한 것으로 생각
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrashType mainTrashType; // 주요 쓰레기 타입

  //  ASSIGNMENT_NEEDED,  // 배정이 필요한 단계 - 화면에 보일예정
  //  ASSIGNMENT_COMPLETED // 배정이 완료된 단계 - 화면에 안보일 예정??
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ReportStatus status = ReportStatus.ASSIGNMENT_NEEDED;


  // 이미지들은 한 폴더, 한 필드에서 관리예정
  // 청소전 사진은 B_202409241052.... 같이 생각 (최소 1장 ~ 최대 5장)
  // 청소후 사진은 A_202409251120.... 같이 생각 (최소 1장 ~ 최대 5장)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 청소 사진들

  // TODO : 아래 내용 나중에 만들기, 지금 하면 헷깔리니까
//  @Column(length = 20, nullable = false)
//  private String weather;
//
//  @Column(length = 20, nullable = false)
//  private String specialNote;


  // 아래는 추가로 필요할것 같은 필드들

  // 여러사람이 들어갈 수 있음

  // 팀원들 ,로 구분할 예정 // 만약 조사를 혼자 갈 수 도 있으니 null 가능하게
  @Column(length = 1000)
  private String members;

  @Column(length = 20, nullable = false)
  private String weather;

  @Column(length = 20, nullable = false)
  private String specialNote;



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

  public void changeStatusToCompleted(ReportStatus reportStatus) {
    this.status = reportStatus;
  }
}
