package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Admin extends Member{

  @Column(length = 40, nullable = false)
  private String workPlace; // 근무처

  @Column(length = 20, nullable = false)
  private String department;  // 부서

  @Column(length = 20, nullable = false)
  private String position; // 직급

  @Column(length = 50, nullable = false)
  private String assignmentArea; // 담당지역  // 담당 지역이 여러개일 수도 있나? 여러개면 enum 쓰는게 나을듯

  @Column(length = 20, unique = true, nullable = false)
  private String contact; // 근무처 연락처

  // 추가로 필요한 필드가 있다면 추가

  // 담당 바닷가 - 이걸 통해서 담당자가 담당하는 바닷가를 특정할 수 있게?
  // 이걸 한 사람이 여러 바닷가를 관리할 수 있을지 하나만 관리할 수 있을지는 생각 해봐야함
  // Research, Clean에 있는 바닷가 이름과 동일한 (enum을 쓸까 생각중) 필드 ex) 길천리 1, 해운대 2 ...


}
