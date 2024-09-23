package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.TrashType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class CleaningReport extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "assignment_id", unique = true, nullable = false)
  private CleaningAssignment assignment;

  @Column(nullable = false)
  private LocalDateTime cleaningTime;

  @Column(nullable = false)
  private String cleaningDetails;

  @ElementCollection
  @Builder.Default
  private List<CleaningImage> imageList = new ArrayList<>();

  // 추가로 필요한 것이 있다면 추가

  // 추가: 실제 수거량 (50L 마대 개수)
  @Column(nullable = false)
  private Integer collectedBagCount;

  // 추가: 주요 쓰레기 종류
  @ElementCollection
  @Enumerated(EnumType.STRING)
  private List<TrashType> mainTrashTypes;

  public void clearList() {
    this.imageList.clear();
  }

}
