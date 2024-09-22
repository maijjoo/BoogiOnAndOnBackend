package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionReport extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "assignment_id", unique = true, nullable = false)
  private CollectionAssignment assignment;

  @Column(nullable = false)
  private LocalDateTime collectionTime;

  // 추가로 필요한 것이 있다면 추가

  // 추가: 실제 수거량, 필요할까??
  @Column(nullable = false)
  private Double actualCollectedVolume;

  // 추가: 이동 경로, 필요할까?
  @Column(columnDefinition = "TEXT")
  private String routeTaken;
}
