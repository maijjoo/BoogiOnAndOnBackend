package com.boogionandon.backend.dto;

import com.boogionandon.backend.domain.enums.MemberRole;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


// User는 스프링 시큐리티를 통해 로그인 결과를 반환할때 필요한 구현체 (UserDetails를 상속받아 구현됨)
// 그래서 이걸 상속 받으면 MemberDTO를 시큐리티 로그인 결과를 반환할때 이걸로 나갈 수 있음
// 그러면 우리가 내보내길 원하는 정보를 내보낼 수 있음 (Custom)
@Getter
public abstract class BaseMemberDTO extends User {

  // 현재 Member를 상속 받아 3가지로 나누어 졌는데 이걸 어떻게 처리할지 고민중
  // 여기에 각각이 가진 모든 정보를 다 넣을지
  // 아니면, DTO를 다 쪼갤지 (이땐 어떻게 딱 원하는 걸 시큐리티 로그인으로 반환할지 고민 필요) - 이걸로 결정
  // 또는 다른 좋은 방법이 있는지

  private Long id;
  private String email;
  private String password;
  private String name;
  private String nickname;
  private String phoneNumber;
  // 권한을 리스트로 줘야하나?
  private MemberRole role;

  public BaseMemberDTO(Long id, String email, String password,
      String name, String nickname, String phoneNumber, MemberRole role) {
    super(email, password, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    this.id = id;
    this.email = email;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.role = role;
  }
}
