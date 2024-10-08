package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickUpDetailResponseDTO {

  private Long id;

  private String submitterName;

  private String pickUpPlace;

  private Double latitude;

  private Double longitude;

  private TrashType mainTrashType;

  private LocalDateTime submitDateTime;

  private Integer realTrashAmount;

  private ReportStatus status;

  private List<String> images;
}
