package com.boogionandon.backend.dto;

import java.time.LocalDate;
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

  private Long id;
  private String username;
  private String password;
  private String email;
  private String name;
  private String phone;
  private LocalDate birth;
  private String address;
  private String addressDetail;

  // 나중에 화면에서 쓰기 쉽게 하기 위해 String으로
  private List<String> roleNames = new ArrayList<>();


  private Double vehicleCapacity; // 차량정보(무게 ton)

  private Long managerId; // 해당 아이디를 만든 관리자
  private boolean delFlag;  // 소프트 딜리트를 위해

  public WorkerDTO(Long id, String username, String password, String email,
      String name,
      String phone, LocalDate birth, String address, String addressDetail, List<String> roleNames,
      Double vehicleCapacity,
      Long managerId, boolean delFlag) {
    super(username, password, roleNames.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(
        Collectors.toList()));
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.birth = birth;
    this.phone = phone;
    this.address = address;
    this.addressDetail = addressDetail;
    this.roleNames = roleNames;
    this.vehicleCapacity = vehicleCapacity;
    this.managerId = managerId;
    this.delFlag = delFlag;
  }


  // 클레임이 JWT로 변환 되어 내보내질 것이니 여기에 password를 안담으면 될듯
  public Map<String, Object> getClaims() {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("id", id);
    dataMap.put("username", username);
    dataMap.put("password", password); // password는 JWT로 변환되지 않아서 이��에 ��어��
    dataMap.put("email", email);
    dataMap.put("name", name);
    dataMap.put("phone", phone);
    dataMap.put("birth", birth);
    dataMap.put("address", address);
    dataMap.put("addressDetail", addressDetail);
    dataMap.put("roleNames", roleNames);
    dataMap.put("vehicleCapacity", vehicleCapacity);
    dataMap.put("managerId", managerId);
    dataMap.put("delFlag", delFlag);
    return dataMap;
  }

  public static WorkerDTO claimsToDTO(Map<String, Object> claims) {
    Long id = claims.get("id") instanceof Number
        ? ((Number) claims.get("id")).longValue()
        : (claims.get("id") instanceof String
            ? Long.parseLong((String) claims.get("id"))
            : null);

    List<String> roleNames = claims.get("roleNames") instanceof List
        ? (List<String>) claims.get("roleNames")
        : new ArrayList<>();

    Double vehicleCapacity = claims.get("vehicleCapacity") instanceof Double
        ? (Double) claims.get("vehicleCapacity")
        : (claims.get("vehicleCapacity") instanceof String
            ? Double.parseDouble((String) claims.get("vehicleCapacity"))
            : 0.0);

    Long managerId = claims.get("managerId") instanceof Number
        ? ((Number) claims.get("managerId")).longValue()
        : (claims.get("managerId") instanceof String
            ? Long.parseLong((String) claims.get("managerId"))
            : null);

    Boolean delFlag = claims.get("delFlag") instanceof Boolean
        ? (Boolean) claims.get("delFlag")
        : Boolean.parseBoolean((String) claims.get("delFlag"));

    // LocalDate 처리
    LocalDate birth = null;
    if (claims.get("birth") != null) {
      if (claims.get("birth") instanceof String) {
        birth = LocalDate.parse((String) claims.get("birth"));
      } else if (claims.get("birth") instanceof LocalDate) {
        birth = (LocalDate) claims.get("birth");
      }
    }

    WorkerDTO dto = new WorkerDTO(
        id,
        (String) claims.get("username"),
        (String) claims.get("password"),
        (String) claims.get("email"),
        (String) claims.get("name"),
        (String) claims.get("phone"),
        birth,
        (String) claims.get("address"),
        (String) claims.get("addressDetail"),
        roleNames,
        vehicleCapacity,
        managerId,
        delFlag
    );

    log.info("WorkerDTO = " + dto);

    return dto;
  }

}
