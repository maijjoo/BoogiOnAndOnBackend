package com.boogionandon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateDTO {

  private String name;

  private String phone;

  private String email;

  private String address;

  private String addressDetail;

  private String department;

  private String position;

  private String contact;
}
