package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Beach {

  @Id
  private String beachName;

  @Column(length = 30, nullable = false)
  private String si;
  @Column(length = 30, nullable = false)
  private String guGun;
  @Column(length = 30, nullable = false)
  private String dongEub;

  // 관지가의 담당 구역과 일치하게 넣기
  @Column(length = 30, nullable = false)
  private String workplace;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

}
