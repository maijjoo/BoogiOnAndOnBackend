package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.MemberType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="d_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Member extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 발제사 쪽에서 관리자가 만들어서 조사자, 청소자, 수거자에게 배포한다 했으니
  // 이메일을 인증받고 할 이유가 없어져서 그냥 이메일 형식이 아닌 일반 username 사용 예정
  @Column(length = 40, unique = true, nullable = false)
  private String username;

  @Column(length = 70, nullable = false)
  private String password;

  @Column(length = 40, unique = true, nullable = false)
  private String email;

  @Column(length = 30, nullable = false)
  private String name;

  @Column(length = 20, nullable = false)
  private String phone;

  @Column(length = 100)
  private String address;

  @Column(length = 150)
  private String addressDetail;

  //  ADMIN, // 관리자
  //  FIELD_WORKER, // 조사자, 청소자
  //  COLLECTOR// 수거자
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private MemberType role;

// 추가로 필요한 필드가 있다면 추가

  private boolean delFlag = false;
}

