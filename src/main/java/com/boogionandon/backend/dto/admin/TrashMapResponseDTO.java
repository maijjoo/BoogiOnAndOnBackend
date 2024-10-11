package com.boogionandon.backend.dto.admin;

import com.boogionandon.backend.domain.enums.TrashType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrashMapResponseDTO {

  private Long id;
  private String cleanerName;
  private String beachName;
  private Integer realTrashAmount;
  private LocalDateTime cleanDateTime;

  private TrashType mainTrashType;

  // 아래 위, 경도는 Beach 엔티티에서 고정으로 주어져 있는 위경도 사용할 예정
  // beachName을 바탕으로 가져올 거임
  // TrashMapResponse에 필요
  private Double fixedLatitude;
  private Double fixedLongitude;

}
