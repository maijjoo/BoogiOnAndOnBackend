package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Member{

  @Column(length = 40, nullable = false)
  private String workPlace; // 근무처

  @Column(length = 20, nullable = false)
  private String department;  // 부서

  @Column(length = 20, nullable = false)
  private String position; // 직급

  @Column(length = 50, nullable = false)
  private String assignmentArea; // 담당지역

  @Column(length = 20, nullable = false)
  private String contact; // 근무처 연락처

  // 추가로 필요한 필드가 있다면 추가

}
