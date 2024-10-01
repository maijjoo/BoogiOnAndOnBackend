package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.TrashType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = {"research", "mainTrashType"})
public class ResearchSub extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "research_main_id", nullable = false)
  private ResearchMain research;

  // 아래 이름은 조사보고서에서 타이틀에 적힌 이름을 기준으로
  // 자동으로 1씩 들어나는 이름입니다.
  // ex) 해운대 1, 해운대 2 ...
  @Column(length = 100, nullable = false)
  private String beachNameWithIndex;

  // 추가: 해안 위치 (위도, 경도)
  @Column(nullable = false)
  private Double startLatitude;  // 위도
  @Column(nullable = false)
  private Double startLongitude; // 경도

  @Column(nullable = false)
  private Double endLatitude;  // 위도
  @Column(nullable = false)
  private Double endLongitude; // 경도

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TrashType mainTrashType; // 주요 쓰레기 타입
  // 이거 어차피 리액트에서 고정시켜서 넘어오는데 enum으로 필요한가?


  // ------------------------------- 아래는 고민중
  // 추가: 해안 길이 (m 단위)
  // (시작 - 끝) 위, 경도로 계산해서 넣기???
  // TODO : dto로 변환할때 거기서 계산해서 넣는걸로 만들어도 되지 않을까?
  private Double researchLength;

  public void setResearch(ResearchMain researchMain) {
    this.research = researchMain;
  }
}
