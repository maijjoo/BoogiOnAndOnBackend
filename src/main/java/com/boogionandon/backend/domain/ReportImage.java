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
public class ReportImage {

  private String fileName;

  //  ord는 이미지의 순서를 관리하는 필드
  private int ord;

  public void setOrd(int ord){
    this.ord = ord;
  }

}
