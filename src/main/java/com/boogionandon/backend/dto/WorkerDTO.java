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
public class WorkerDTO extends User {

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

  private String contact; // 근무처 연락처
  private String workGroup; // 소속
  private String workAddress; //// 소속 주소
  private String workAddressDetail; // 소속 상세 주소

  private int vehicleCapacity; // 차량정보(무게 ton)

  private Long managerId; // 해당 아이디를 만든 관리자
  private boolean delFlag;  // 소프트 딜리트를 위해

  public WorkerDTO(String username, String password, String email,
      String name,
      String phone, String address, String addressDetail, List<String> roleNames, String contact,
      String workGroup, String workAddress, String workAddressDetail, int vehicleCapacity,
      Long managerId, boolean delFlag) {
    super(username, password, roleNames.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(
        Collectors.toList()));
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.address = address;
    this.addressDetail = addressDetail;
    this.roleNames = roleNames;
    this.contact = contact;
    this.workGroup = workGroup;
    this.workAddress = workAddress;
    this.workAddressDetail = workAddressDetail;
    this.vehicleCapacity = vehicleCapacity;
    this.managerId = managerId;
    this.delFlag = delFlag;
  }


  // 클레임이 JWT로 변환 되어 내보내질 것이니 여기에 password를 안담으면 될듯
  public Map<String, Object> getClaims() {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("username", username);
    dataMap.put("password", password); // password는 JWT로 변환되지 않아서 이��에 ��어��
    dataMap.put("email", email);
    dataMap.put("name", name);
    dataMap.put("phone", phone);
    dataMap.put("address", address);
    dataMap.put("addressDetail", addressDetail);
    dataMap.put("roleNames", roleNames);
    dataMap.put("contact", contact);
    dataMap.put("workGroup", workGroup);
    dataMap.put("workAddress", workAddress);
    dataMap.put("workAddressDetail", workAddressDetail);
    dataMap.put("vehicleCapacity", vehicleCapacity);
    dataMap.put("managerId", managerId);
    dataMap.put("delFlag", delFlag);
    return dataMap;
  }

  public static WorkerDTO claimsToDTO(Map<String, Object> claims) {
    log.info("claims.password : " +claims.get("password"));
    List<String> roleNames = claims.get("roleNames") instanceof List
        ? (List<String>) claims.get("roleNames")
        : new ArrayList<>();

    Integer vehicleCapacity = claims.get("vehicleCapacity") instanceof Integer
        ? (Integer) claims.get("vehicleCapacity")
        : (claims.get("vehicleCapacity") instanceof String
            ? Integer.parseInt((String) claims.get("vehicleCapacity"))
            : 0);

    Long managerId = claims.get("managerId") instanceof Number
        ? ((Number) claims.get("managerId")).longValue()
        : (claims.get("managerId") instanceof String
            ? Long.parseLong((String) claims.get("managerId"))
            : null);

    Boolean delFlag = claims.get("delFlag") instanceof Boolean
        ? (Boolean) claims.get("delFlag")
        : Boolean.parseBoolean((String) claims.get("delFlag"));

    WorkerDTO dto = new WorkerDTO(
        (String) claims.get("username"),
        (String) claims.get("password"),
        (String) claims.get("email"),
        (String) claims.get("name"),
        (String) claims.get("phone"),
        (String) claims.get("address"),
        (String) claims.get("addressDetail"),
        roleNames,
        (String) claims.get("contact"),
        (String) claims.get("workGroup"),
        (String) claims.get("workAddress"),
        (String) claims.get("workAddressDetail"),
        vehicleCapacity,
        managerId,
        delFlag
    );

    log.info("WorkerDTO = " + dto);

    return dto;
  }

}
