package com.boogionandon.backend.domain;

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
import jakarta.persistence.OneToOne;
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
public class Clean extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "research_id", unique = true, nullable = false)
  private Research research; // 조사된 리포트를 바탕으로 진행하기 위해 연결

  // 그런데 보통 단체로 가서 하지 않나??
  // 이건 대표자라 하고 참가한 사람들의 이름을 넣어주는 List가 있어야 할까?
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cleaner_id", nullable = false)
  private RegularMember cleaner; // 청소자, 청소를 하고 이 보고서를 올린 로그인 자

  // 이 리포트를 관리하는 관리자
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", nullable = false)
  private Admin admin; // 관리자, 조사에서 청소자를 배정시켰던 관리자

  // 해안명과 길이는 조사된 걸 바탕으로 할 테니 조사 리포트에서 가져오면 (자동저장)시키면 안된나?
  @Column(length = 20, nullable = false)
  private String beachName;
  // 추가: 해안 길이 (m 단위)
  @Column(nullable = false)
  private Double beachLength; // ex) 19.2m

  @Column(nullable = false)
  private Integer realTrashAmount; // 실제 쓰레기 양 (ex - 50L쓰레기 봉투를 기준으로 갯수로 계산 예정)

  // 청소일시는 자동? 수동?
  // 자동이면 사진을 찍을때? 폼을 전송할때?
  @Column(nullable = false)
  private LocalDateTime cleanDateTime;

  // 위도,경도는 자동 사진 찍을때? 폼을 전송할때?
  @Column(nullable = false)
  private Double cleanLatitude;  // 청소위치 위도
  @Column(nullable = false)
  private Double cleanLongitude; // 청소위치 경도

  // 주요 쓰레기는 조사내용과 일치하는지 파악하기 위해 필요한 것으로 생각
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrashType mainTrashType; // 주요 쓰레기 타입

  // 위도,경도는 자동 사진 찍을때? 폼을 전송할때?
  @Column(nullable = false)
  private Double pickupLatitude;  // 집하장소 위치 위도
  @Column(nullable = false)
  private Double pickupLongitude; // 집하장소 위치 경도


  // 이미지들은 한 폴더, 한 필드에서 관리예정
  // 청소전 사진은 B_202409241052.... 같이 생각 (최소 5장 ~ 최대 20장)
  // 청소후 사진은 A_202409251120.... 같이 생각 (최소 5장 ~ 최대 20장)
  // 집하장소 사진은 C_202409251220.... 같이 생각 (3장 이하)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 청소 사진들과 집하장소 사진

  // 모아놓은 쓰레기를 가져갈 수거자
  // 이걸 배정으로 넣어야 할지, 배정된 지역에 있는 수거자가 지도의 핀을 클릭해 수거하겠다고 하면
  // 그때 배정이 되어야 하는지??? // 나중에 들어 갈 것 같아서 밑으로 내려놓음
  // 일단 배정이 언제될지 모르기 때문에 null가능으로 만들어놓음
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collector_id")
  private RegularMember collector; // 수거자,

  // 아래는 추가로 필요할것 같은 필드들

  @Column(nullable = false)
  private LocalDateTime cleaningTime;

}
