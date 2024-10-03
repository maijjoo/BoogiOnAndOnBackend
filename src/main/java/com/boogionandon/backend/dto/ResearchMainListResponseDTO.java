package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchMainListResponseDTO {

  private Long id;
  private String researcherName;

  // TODO : 같이 일한사람도 넣어야 하나?
  // List화면 보여주는 곳에서는 sub에 관한거 필요 없을 듯
  private String beachName;

  private Double totalBeachLength; // 청소한 총 길이

  private Integer expectedTrashAmount; // 청소한 총 량 ex) 50L

  private LocalDateTime reportTime;

  private String thumbnail;

  private ReportStatus status;

  private String weather;

  private String specialNote;



}
