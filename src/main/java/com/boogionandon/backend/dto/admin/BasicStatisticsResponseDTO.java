package com.boogionandon.backend.dto.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicStatisticsResponseDTO {

  // 연도별 데이터가 들어가 (lastYear - 4 ~ lastYear)
  @Builder.Default
  List<FiveYearAgoToLastYearDTO> years = new ArrayList<>();

  // 해당 연도의 데이터들을 다 가져와 월별 데이터 만들기
  @Builder.Default
  List<MonthlyDataForTheYearDTO> monthly = new ArrayList<>();

  // 해당 월의 데이터들을 다 가져와 일별 데이터 만들기
  @Builder.Default
  List<DaysDataForTheMonthDTO> days = new ArrayList<>();

  // 아래 필드는 guGun, 그리고 beachName을 자동완성으로 찾기 위해 내리는 데이터
  List<String> guGun = null;

  Map<String, List<String>> beachName = null;
}
