package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.ReportStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
@ToString(exclude = {"researcher", "researchSubList"})
public class ResearchMain extends BaseEntity{

  // 우리는 이제 조사 보고서랑 청소보고서랑 연결이 안되는 쪽으로 잡음
  // 유선으로 청소자를 배정 한다고 했으니
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 조사 보고서를 올린사람
  // 조사 보고서를 올릴때 로그인 한사람이 자동으로 들어가게 만들예정
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "researcher_id", nullable = false)
  private Worker researcher; // 조사자

//  // 해변이름은 조사자가 임의로 넣습니다.
//  // 이 이름은 보고서의 타이틀에서 이용된 해변이름입니다. 타이틀에서 뒤에 + 바닷가 해줄 예정
//  // ex) 해운대
//  @Column(length = 20, nullable = false)
//  private String beachName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "beach_id", nullable = false)
  private Beach beach; // 해안

  @OneToMany(mappedBy = "research", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ResearchSub> researchSubList = new ArrayList<>();

  // 추가: 해안 길이 (m 단위)
  // 위 필드와 동일??? // 아니면 임의로 적게 만들기
  @Column(nullable = false)
  private Double totalBeachLength; // ex) 서브에서 구한 길이들의 총합

  // 추가: 예상 쓰레기 수거량 (L 단위)
  @Column(nullable = false)
  private Integer expectedTrashAmount; // ex) 50L

  @Column(nullable = false)
  private LocalDateTime reportTime; // 제출시 자동 입력

  // 조사 사진은 R_202409241052.... 같이 생각 (최소 5장 ~ 최대 20장)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 조사된 이미지들


  //  ASSIGNMENT_NEEDED,  // 배정이 필요한 단계 - 화면에 보일예정
  //  ASSIGNMENT_COMPLETED // 배정이 완료된 단계 - 화면에 안보일 예정??
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ReportStatus status = ReportStatus.ASSIGNMENT_NEEDED;

  // --------

  // 조사자에는 이미 조사관을 담당하는 관리자가 있어서
  // 여기서 조사 보고서에서 넣을 필요는 없을 듯

  // 날씨(자동), 특이사항(수동),

  @Column(length = 20, nullable = false)
  private String weather;

  @Column(length = 20, nullable = false)
  private String specialNote;

  // 팀원들 ,로 구분할 예정 // 만약 조사를 혼자 갈 수 도 있으니 null 가능하게
  @Column(length = 1000)
  private String members;


  public void addImage(Image image) {
    image.setOrd(images.size());
    images.add(image);
  }

  public void addImageString(String fileName) {
    Image image = Image.builder()
        .fileName(fileName)
        .build();
    addImage(image);
  }

  public void addResearchSubList(ResearchSub researchSub) {
    // 아래 코드는 main의 List에 넣는것
    this.researchSubList.add(researchSub);
    // 아래 코드는 sub의 ResearchMain research에 넣는것
    // 그래서 sub에서는 따로 더 넣을 필요 없음
    researchSub.setResearch(this);
  }

  public void setTotalResearch(Double totalBeachLength) {
    this.totalBeachLength = totalBeachLength;
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
