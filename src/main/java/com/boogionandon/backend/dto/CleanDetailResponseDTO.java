package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanDetailResponseDTO {

  private Long id;

  private String cleanerName;

  private String beachName;

  private Integer realTrashAmount;

  private LocalDateTime cleanDateTime;

  private Double startLatitude;

  private Double startLongitude;

  private Double endLatitude;

  private Double endLongitude;

  private Double beachLength;

  private TrashType mainTrashType;

  private ReportStatus status;

  private List<String> images;

  private List<String> members;

  private String weather;

  private String specialNote;

}
