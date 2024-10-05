package com.boogionandon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRequestDTO {

  @Builder.Default
  private int page = 1;

  @Builder.Default
  private int size = 10;

  // 추가로 필요한 검색정보 같은 것들 필요하면 추가하기
  @Builder.Default
  private String sort = "desc";

}
