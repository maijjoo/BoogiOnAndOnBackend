package com.boogionandon.backend.dto.admin;

import com.boogionandon.backend.domain.enums.TrashType;
import java.time.LocalDateTime;
import java.util.List;
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
  private String cleanerUsername;
  private String beachName;
  private Integer realTrashAmount;
  private LocalDateTime cleanDateTime;

  // 여기 위경도는 시작 ~ 끝 위치 보여주는 곳
  private Double startLatitude;
  private Double startLongitude;
  private Double endLatitude;
  private Double endLongitude;

  private Double beachLength;
  private TrashType mainTrashType;
  private List<String> beforeImageUrls;
  private List<String> afterImageUrls;

  // 아래 위, 경도는 Beach 엔티티에서 고정으로 주어져 있는 위경도 사용할 예정
  // beachName을 바탕으로 가져올 거임
  // TrashMapResponse에 필요
  private Double fixedLatitude;
  private Double fixedLongitude;

}
