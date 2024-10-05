package com.boogionandon.backend.controller;

import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;
import com.boogionandon.backend.service.MemberService;
import com.boogionandon.backend.util.CustomJWTException;
import com.boogionandon.backend.util.JWTUtil;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;


  @GetMapping("/my-page/worker/{workerId}")
  public WorkerResponseDTO getWorkerProfile(@PathVariable("workerId") Long workerId) {
    log.info("workerId: " + workerId);
    return memberService.getWorkerProfile(workerId);
  }

  @GetMapping("/my-page/admin/{adminId}")
  public AdminResponseDTO getAdminProfile(@PathVariable("adminId") Long adminId) {
    log.info("adminId: " + adminId);
    return memberService.getAdminProfile(adminId);
  }

  public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {

    if(refreshToken == null) {
      throw new CustomJWTException("NULL_REFRESH");
    }

    if(authHeader == null || authHeader.length() < 7) {
      throw new CustomJWTException("INVALID STRING");
    }

    // Bearer xxxx....
    String accessToken = authHeader.substring(7);

    // AccessToken 의 만료 여부 확인
    if(checkExpiredToken(accessToken) == false) {
      return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    // Refresh 토큰 검증
    Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

    log.info("refresh ... claims : " + claims);

    String newAccessToken = JWTUtil.generateToken(claims, 10);

    String newRefreshToken = checkTime((Integer)claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60*24) : refreshToken;

    return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
  }

  // 시간이 1시간 미만으로 남았다면...
  private boolean checkTime(Integer exp) {

    // JWT exp를 날짜로 변환
    Date expDate = new Date((long) exp * (1000));

    // 현재 시간과의 차이 계산 - 밀리세컨즈
    long gap = expDate.getTime() - System.currentTimeMillis();

    // 분단위 계산
    long leftMin = gap / (1000 * 60);

    // 1시간도 안남았는지
    return leftMin < 60;
  }

  private boolean checkExpiredToken (String token) {
    try {
      JWTUtil.validateToken(token);
    } catch (CustomJWTException ex) {
      if(ex.getMessage().equals("Expired")){
        return true;

      }
    }
    return false;
  }

}
