package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.CollectionPointStatus;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CollectionPoint extends BaseEntity {

  // 이 엔티티는 만약 관리자에서 수거자를 배정하지 않고 할때 사용
  // 이폼을 사용해 수거자 지도에 등록이 되는 형태?
  // 만약 쓰게 되면 청소자가 CleaningReport 처리후,
  // 따로 이 폼을 사용해 집하장에 쓰레기를 가져다 놓은 후
  // 청소자가 따로 이 엔티티를 사용해 등록
  // 그렇게 되면 CollectionAssignment, CollectionReport는 필요 없지 않을 까???

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "cleaner_id", nullable = false)
  private FieldWorker cleaner;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Column(nullable = false)
  private Double estimatedVolume;

  @Enumerated(EnumType.STRING)
  private CollectionPointStatus status;

  @ElementCollection
  @Builder.Default
  private List<CollectionPointImage> imageList = new ArrayList<>();

  // 이미지 리스트 초기화 메서드
  public void clearImageList() {
    this.imageList.clear();
  }

  // 이미지 추가 메서드
  public void addImage(CollectionPointImage image) {
    this.imageList.add(image);
  }

  // 이미지 제거 메서드
  public void removeImage(CollectionPointImage image) {
    this.imageList.remove(image);
  }
}