package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpListForCollectorResponseDTO {

  private Long id;

  private String submmiterName;

  private String pickUpPlace;

  private Double latitude;
  private Double longitude;

  private TrashType mainTrashType;

  private Double actualCollectedVolume; // 50L 쓰레기 봉투 갯수

  private List<String> images;

  private ReportStatus status;



}
