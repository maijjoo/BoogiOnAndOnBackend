package com.boogionandon.backend.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {

  // Member에 관련된 필드들

  private Long id;

  private String username;

  private String email;

  private String name;

  private String phone;

  private String address;

  private String addressDetail;

  // Admin에 관련된 필드들

  private String workCity;

  private String workPlace;

  private List<String> assignmentAreaList;

  private String department;

  private String position;

  private String contact;

}
