package com.boogionandon.backend.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


public class AdminDTO extends User {

  // id는 쓰일일도 보여줄일도 없을 것 같아서 안올림

  private String username;
  private String password;
  private String email;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;

  // 나중에 화면에서 쓰기 쉽게 하기 위해 String으로
  private List<String> roleNames = new ArrayList<>();

  private String workPlace; // 근무처
  private String department;  // 부서
  private String position; // 직급
  private String assignmentArea; // 담당지역
  private String contact; // 근무처 연락처

  private Long managerId; // 해당 아이디를 만든 관리자
  private boolean delFlag;  // 소프트 딜리트를 위해

  public AdminDTO(String username, String password, String email,
      String name,
      String phone, String address, String addressDetail, List<String> roleNames, String workPlace,
      String department, String position, String assignmentArea, String contact, Long managerId,
      boolean delFlag) {
    super(username, password, roleNames.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role)).collect(
        Collectors.toList()));

    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.address = address;
    this.addressDetail = addressDetail;
    this.roleNames = roleNames;
    this.workPlace = workPlace;
    this.department = department;
    this.position = position;
    this.assignmentArea = assignmentArea;
    this.contact = contact;
    this.managerId = managerId;
    this.delFlag = delFlag;
  }


  // 클레임이 JWT로 변환 되어 내보내질 것이니 여기에 password를 안담으면 될듯
  public Map<String, Object> getClaims() {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("username", username);
    dataMap.put("email", email);
    dataMap.put("name", name);
    dataMap.put("phone", phone);
    dataMap.put("address", address);
    dataMap.put("addressDetail", addressDetail);
    dataMap.put("roleNames", roleNames);
    dataMap.put("workPlace", workPlace);
    dataMap.put("department", department);
    dataMap.put("position", position);
    dataMap.put("assignmentArea", assignmentArea);
    dataMap.put("contact", contact);
    dataMap.put("managerId", managerId);
    dataMap.put("delFlag", delFlag);
    return dataMap;
  }

  public static AdminDTO claimsToDTO(Map<String, Object> claims) {
    return new AdminDTO(
        (String) claims.get("username"),
        (String) claims.get("password"),
        (String) claims.get("email"),
        (String) claims.get("name"),
        (String) claims.get("phone"),
        (String) claims.get("address"),
        (String) claims.get("addressDetail"),
        (List<String>) claims.get("roleNames"),
        (String) claims.get("workPlace"),
        (String) claims.get("department"),
        (String) claims.get("position"),
        (String) claims.get("assignmentArea"),
        (String) claims.get("contact"),
        (Long) claims.get("managerId"),
        (boolean) claims.get("delFlag"));
  }

}
