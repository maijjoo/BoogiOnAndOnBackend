package com.boogionandon.backend.dto.member;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerResponseDTO {

  // Member에 관련된 필드들

  private Long id;

  private String username;

  private String email;

  private String name;

  private String phone;

  private String address;

  private String addressDetail;

  // Worker에 관련된 필드들

  private String contact; // 근무처 연락처, 근무처? 가 중복일 수도 있을거 같아서 unique 안걸음

  private String workGroup; // 소속, TODO : 이건 뭐에대한 소속이지?

  private String workAddress; //// 소속 주소

  private String workAddressDetail; // 소속 상세 주소

  private int vehicleCapacity; // 차량정보(무게 ton)

  private LocalDate startDate;

  private LocalDate endDate;

  // 관리자에 의해서 어디에 소속 되어있는지 볼려면?
  private Long managerId;

  private String managerName;

  private String managerDepartment; // 부서

  private String managerContact; // phone 번호 말고, 부서의 전화번호

  private String managerWorkPlace;

  // 관리자 Worker 둘다 필요하니까 manager 빼고 이렇게 넣음
  private List<String> assignmentAreaList;

}
