package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.AssignmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CollectionAssignment extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "cleaning_report_id", unique = true, nullable = false)
  private CleaningReport cleaningReport;

  @ManyToOne
  @JoinColumn(name = "assigned_collector_id", nullable = false)
  private Member assignedCollector;

  @Column(nullable = false)
  private LocalDateTime assignmentTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AssignmentStatus status;

  // 여기서는 관리자가 수거자를 배정해주는 형태로 하고 있지만
  // 청소자가 청소를 끝내고 보내는 폼이 있고
  // 청소를 끝내고 집하장 같은데 모아 놓는 곳을 올리는 폼이 하나 더 있어야 하지 않을까?
  // 이경우에는 수거자가 배정은 안되고 해당 지역의 수거자는 해당지역의 것만 보이고
  // 청소자가 올린 집하장 폼을 근거로 지도에 모아놓은 장소가 찍히고 픽업을 하는 식으로
}
