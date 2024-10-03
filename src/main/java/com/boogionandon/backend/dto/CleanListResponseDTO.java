package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanListResponseDTO {

  private Long id;

  private String cleanerName;
  private String beachName;

  private Integer realTrashAmount; // 실제 쓰레기 양 (ex - 50L쓰레기 봉투를 기준으로 갯수로 계산 예정)

  private LocalDateTime cleanDateTime;

  // 위, 경도는 List 에서는 안보여 줘도 될듯

  private Double beachLength; // ex) 19.2m 단건의 결과

  private TrashType mainTrashType;

  private ReportStatus status;

  private String thumbnail;

}
