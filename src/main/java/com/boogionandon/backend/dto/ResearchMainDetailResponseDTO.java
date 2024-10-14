package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
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
public class ResearchMainDetailResponseDTO {

  private Long id;

  private String researcherName;

  private String beachName;

  private Double totalBeachLength;

  private Integer expectedTrashAmount;

  private LocalDateTime reportTime;

  private ReportStatus status;

  private String weather;

  private String specialNote;

  private List<String> images;

  private List<ResearchSubDetailResponseDTO> researchSubList;

  private List<String> members;
}
