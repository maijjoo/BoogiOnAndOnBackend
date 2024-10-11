package com.boogionandon.backend.security.handler;

import com.boogionandon.backend.dto.AdminDTO;
import com.boogionandon.backend.dto.WorkerDTO;
import com.boogionandon.backend.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


// 인증에 성공했을때 (로그인) 어떻게 해줄거야
// 나중에 config 쪽에서 추가해서 쓸거기 때문에 @Configuration 필요 없음
@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  public APILoginSuccessHandler() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }


  /**
   * 이 클래스는 사용자가 로그인할 때 인증 성공 이벤트를 처리합니다.
   * 인증 객체에서 회원정보를 추출하고,
   *는 이를 JSON 문자열로 변환하고 응답으로 다시 보냅니다.
   *
   * @author 귀하의 이름
   * @1.0부터
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    log.info("...............APILoginSuccessHandler.................");
    // 로그인에 성공하면 인증 객체(authentication)에 memberDTO가 포함됩니다.
    // 그런 다음 JWT에 대한 클레임으로 변환하기 위해 추출됩니다.
    log.info("authentication", authentication);
    log.info(".....................");

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Map<String, Object> claims = null;

    // 아래가 안되면... 이름의 앞자리로 해보거나, Role 로 찾아보기
    if (userDetails.getClass().getSimpleName().contains("Admin")) {
      log.info("Logged in user is an Admin");
      AdminDTO adminDTO = (AdminDTO) authentication.getPrincipal();
      claims = adminDTO.getClaims();
    } else if (userDetails.getClass().getSimpleName().contains("Worker")) {
      log.info("Logged in user is a Worker");
      WorkerDTO workerDTO = (WorkerDTO) authentication.getPrincipal();
      claims = workerDTO.getClaims();
    }

    // 이 토큰은 보안을 강화하기 위해 사용됩니다. 탈취되더라도 피해를 최소화할 수 있습니다.
    // 현재는 5시간 으로 고정
    String accessToken = JWTUtil.generateToken(claims, 60 * 5);
    // 액세스 토큰이 만료될 때 새로운 액세스 토큰을 발급받는 데 사용됩니다.
    // 현재는 100일로 설정, 이걸로 자동 로그인 해야할 듯
    String refreshToken = JWTUtil.generateToken(claims, 60*24*100);

    claims.put("accessToken", accessToken);
    claims.put("refreshToken", refreshToken);

    // 생성된 클레임은 Jackson 라이브러리를 사용하여 JSON 문자열로 변환됩니다.
    String jsonStr = objectMapper.writeValueAsString(claims);


    // JSON 문자열은 응답 콘텐츠 유형으로 설정되어 클라이언트로 다시 전송됩니다.
    response.setContentType("application/json; charset=UTF-8");

    // PrintWriter를 사용하여 JSON 문자열을 http body로 전송합니다.
    PrintWriter printWriter = response.getWriter();
    printWriter.print(jsonStr);
    printWriter.close();

  }
}
