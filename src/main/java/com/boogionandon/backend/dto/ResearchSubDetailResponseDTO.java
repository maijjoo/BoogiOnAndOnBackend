package com.boogionandon.backend.dto;


import com.boogionandon.backend.domain.enums.TrashType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchSubDetailResponseDTO {

  private Long id;

  // 아래 이름은 조사보고서에서 타이틀에 적힌 이름을 기준으로
  // 자동으로 1씩 들어나는 이름입니다.
  // ex) 해운대 1, 해운대 2 ...
  private String beachNameWithIndex;

  private Double startLatitude;  // 시작 위도
  private Double startLongitude; // 시작 경도

  private Double endLatitude;  // 끝 위도
  private Double endLongitude; // 끝 경도

  private TrashType mainTrashType; // 주요 쓰레기 타입

  private Double researchLength;
}
