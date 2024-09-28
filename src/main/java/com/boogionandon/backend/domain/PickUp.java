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
public class PickUp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submitter_id", nullable = false)
  private Worker submitter; // 청소자, 청소를 하고 이 보고서를 올린 로그인 자

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
  private LocalDateTime submitDateTime;

  // TODO : 이걸 청소자가 작성할지, 수거자가 작성할지 아니면 각각 다 입력할지
  @Column(nullable = false)
  private Double actualCollectedVolume;

  // 집하장소 사진은 C_202409251220.... 같이 생각 (3장 이하)
  @ElementCollection
  @Builder.Default
  @Column(nullable = false)
  private List<Image> images = new ArrayList<>(); // 집하장소 사진


  // TODO : 아래 필드가 필요 할까?
  // 널이었다가 추후 수거자가 배정하면 들어가는 걸로 생각중
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collector_id", nullable = true)
  private Worker collector; // 수거자

  // 수거 시간, 수거자가 수거해갈때의 시간
  @Column
  private LocalDateTime collectDateTime;


}
