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
public class CleaningAssignment extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "report_id", unique = true, nullable = false)
  private InvestigationReport report;

  @ManyToOne
  @JoinColumn(name = "assigned_cleaner_id", nullable = false)
  private Member assignedCleaner;

  @Column(nullable = false)
  private LocalDateTime assignmentTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AssignmentStatus status;

}
