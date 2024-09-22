package com.boogionandon.backend.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleaningImage {

  private String fileName;

  //  ord는 이미지의 순서를 관리하는 필드
  private int ord;

  public void setOrd(int ord){
    this.ord = ord;
  }

  // 타입 (BEFORE, AFTER)은 따로 지정 안하고 사진을 올릴때 구역에 따라 (BEFORE, AFTER)
  // 앞에 Before_, After_ 같이 붙여서 이름을 저장할 생각중
}
