package com.boogionandon.backend.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  // 일하는 지역
  @Column(length = 20, nullable = false)
  private String workCity;

  // 근무처
  // 일반 관리자는 구청이라고 생각하고 -> Beach 테이블의 guGun
  // 아이디를 만들때 Beach 테이블의 guGun 값을 Set 같은 걸로 내려주고
  // 그리고 guGun에 해당하는 guGun이 일치하는 beachName을 넣어줄 예정
  // 거기서 자동 완성 같은 걸로 하는게 낫지 않을까? (관리자가 회원가입 시키는 페이지에서 필요)
  @Column(length = 40, nullable = false)
  private String workPlace;

  // guGun : [guGun이 일치하는 beachName (담당지역)]
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "admin_assignment_areas",
      joinColumns = @JoinColumn(name = "admin_id")
  )
  @Column(name = "area", length = 50)
  @Builder.Default // 에러나면 이거 때문 일 수 도...
  private List<String> assignmentAreaList = new ArrayList<>(); // 담당지역

  @Column(length = 20, nullable = false)
  private String department;  // 부서

  @Column(length = 20, nullable = false)
  private String position; // 직급



  @Column(length = 20, unique = true, nullable = false)
  private String contact; // 근무처 연락처

  public void updateDepartment(String department) {
    this.department = department;
  }

  public void updatePosition(String position) {
    this.position = position;
  }

  public void updateContact(String contact) {
    this.contact = contact;
  }

  // 추가로 필요한 필드가 있다면 추가

  // 담당 바닷가 - 이걸 통해서 담당자가 담당하는 바닷가를 특정할 수 있게?
  // 이걸 한 사람이 여러 바닷가를 관리할 수 있을지 하나만 관리할 수 있을지는 생각 해봐야함
  // Research, Clean에 있는 바닷가 이름과 동일한 (enum을 쓸까 생각중) 필드 ex) 길천리 1, 해운대 2 ...


}
