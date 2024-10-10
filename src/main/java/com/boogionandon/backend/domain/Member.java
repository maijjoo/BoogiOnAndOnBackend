package com.boogionandon.backend.domain;

import com.boogionandon.backend.domain.enums.MemberType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="d_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"password", "memberRoleList"})
public abstract class Member extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 발제사 쪽에서 관리자가 만들어서 조사자, 청소자, 수거자에게 배포한다 했으니
  // 이메일을 인증받고 할 이유가 없어져서 그냥 이메일 형식이 아닌 일반 username 사용 예정
  @Column(length = 40, unique = true, nullable = false)
  private String username; // 아이디

  @Column(length = 70, nullable = false)
  private String password;

  @Column(length = 40, unique = true, nullable = false)
  private String email;

  @Column(length = 30, nullable = false)
  private String name;

  @Column(length = 20, unique = true, nullable = false)
  private String phone;

  @Column(length = 100)
  private String address;

  @Column(length = 150)
  private String addressDetail;

  //  SUPER_ADMIN, // 관리자를 만들 수 있는 관리자, ADMIN 권한도 넣어줘야함
  //  ADMIN, // 관리자
  //  WORKER, // 조사자, 청소자, 수거자
  @ElementCollection(fetch = FetchType.LAZY)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private List<MemberType> memberRoleList = new ArrayList<>();

  // TODO : 협의 필요
  // Member나 Admin을 바로 넣으면 순환 참조 발생
  // Member가 Admin에 의존하고, Admin이 다시 Member에 의존하는 구조
  // 이를 해결하려면 ManagerId를 Member Entity에 추가하고, Admin Entity에 ManagerId를 Foreign Key로 설정
  // 위의 경우 불러오기 위해쓸때는 생각을 좀 해보고 써야 할듯
  // service에서 아래처럼 불러와야 할듯
  // @Autowired
  //    private AdminRepository adminRepository;
  //  public Admin getManagerForMember(Member member) {
  //        return adminRepository.findById(member.getManagerId());
  //    }
  // 아니면 다른 방법도 고려중
  // 수퍼 관리자는 아마도 null 값일듯
  @Column(name = "manager_id")
  private Long managerId;

// 추가로 필요한 필드가 있다면 추가

  @Builder.Default
  private boolean delFlag = false;

  public void changeDelFlag(boolean delFlag) {
    this.delFlag = delFlag;
  }

  public void updatePhone(String phone) {
    this.phone = phone;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateAddress(String address) {
    this.address = address;
  }

  public void updateAddressDetail(String addressDetail) {
    this.addressDetail = addressDetail;
  }
}

