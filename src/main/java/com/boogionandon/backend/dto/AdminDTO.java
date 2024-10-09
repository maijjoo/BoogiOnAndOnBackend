package com.boogionandon.backend.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Slf4j
public class AdminDTO extends User {

  private Long id;
  private String username;
  private String password;
  private String email;
  private String name;
  private String phone;
  private String address;
  private String addressDetail;

  // 나중에 화면에서 쓰기 쉽게 하기 위해 String으로
  private List<String> roleNames = new ArrayList<>();

  private String workCity;
  private String workPlace; // 근무처
  private String department;  // 부서
  private String position; // 직급

  // 생성자 할때 같이 넣으니까 계속 에러남 // 따로 sql짜서 넣기
  private List<String> assignmentAreaList; // 담당지역
  private String contact; // 근무처 연락처

  private Long managerId; // 해당 아이디를 만든 관리자
  private boolean delFlag;  // 소프트 딜리트를 위해

  public AdminDTO(Long id, String username, String password, String email,
      String name,
      String phone, String address, String addressDetail, List<String> roleNames, String workCity,String workPlace,
      String department, String position, String contact, Long managerId,
      boolean delFlag) {
    super(username, password, roleNames.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role)).collect(
        Collectors.toList()));

    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.address = address;
    this.addressDetail = addressDetail;
    this.roleNames = roleNames;
    this.workCity = workCity;
    this.workPlace = workPlace;
    this.department = department;
    this.position = position;
    this.contact = contact;
    this.managerId = managerId;
    this.delFlag = delFlag;
  }


  // 클레임이 JWT로 변환 되어 내보내질 것이니 여기에 password를 안담으면 될듯
  public Map<String, Object> getClaims() {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("id", id);
    dataMap.put("username", username);
    dataMap.put("password", password);
    dataMap.put("email", email);
    dataMap.put("name", name);
    dataMap.put("phone", phone);
    dataMap.put("address", address);
    dataMap.put("addressDetail", addressDetail);
    dataMap.put("roleNames", roleNames);
    dataMap.put("workCity", workCity);
    dataMap.put("workPlace", workPlace);
    dataMap.put("department", department);
    dataMap.put("position", position);
    // Remove assignmentAreaList from claims
    // dataMap.put("assignmentAreaList", assignmentAreaList);
    dataMap.put("contact", contact);
    dataMap.put("managerId", managerId);
    dataMap.put("delFlag", delFlag);
    return dataMap;
  }

  public static AdminDTO claimsToDTO(Map<String, Object> claims) {
    log.info("Processing claims to DTO");

    List<String> roleNames = getListFromClaims(claims, "roleNames");

    Long id = getIdFromClaims(claims);

    Long managerId = getManagerIdFromClaims(claims);

    Boolean delFlag = getBooleanFromClaims(claims, "delFlag");

    // Remove or comment out this line if you decide to exclude assignmentAreaList from JWT
    // List<String> assignmentAreaList = getListFromClaims(claims, "assignmentAreaList");

    AdminDTO dto = new AdminDTO(
        id,
        (String) claims.get("username"),
        (String) claims.get("password"),
        (String) claims.get("email"),
        (String) claims.get("name"),
        (String) claims.get("phone"),
        (String) claims.get("address"),
        (String) claims.get("addressDetail"),
        roleNames,
        (String) claims.get("workCity"),
        (String) claims.get("workPlace"),
        (String) claims.get("department"),
        (String) claims.get("position"),
        (String) claims.get("contact"),
        managerId,
        delFlag
    );

    log.info("AdminDTO created: " + dto);
    return dto;
  }

  private static List<String> getListFromClaims(Map<String, Object> claims, String key) {
    Object value = claims.get(key);
    if (value instanceof List) {
      return (List<String>) value;
    }
    log.warn(key + " is not a List in claims");
    return new ArrayList<>();
  }

  private static Long getManagerIdFromClaims(Map<String, Object> claims) {
    Object value = claims.get("managerId");
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    if (value instanceof String) {
      try {
        return Long.parseLong((String) value);
      } catch (NumberFormatException e) {
        log.warn("Failed to parse managerId: " + value, e);
      }
    }
    log.warn("managerId is not a Number or String in claims");
    return null;
  }
  private static Long getIdFromClaims(Map<String, Object> claims) {
    Object value = claims.get("id");
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    if (value instanceof String) {
      try {
        return Long.parseLong((String) value);
      } catch (NumberFormatException e) {
        log.warn("Failed to parse id: " + value, e);
      }
    }
    log.warn("id is not a Number or String in claims");
    return null;
  }

  private static Boolean getBooleanFromClaims(Map<String, Object> claims, String key) {
    Object value = claims.get(key);
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    if (value instanceof String) {
      return Boolean.parseBoolean((String) value);
    }
    log.warn(key + " is not a Boolean or String in claims");
    return false;
  }
  // 생성자 할때 같이 넣으니까 계속 에러남 // 따로 sql짜서 넣기
  public void setAssignmentAreaList(List<String> assignmentAreaList) {
    this.assignmentAreaList = assignmentAreaList;
  }
}
