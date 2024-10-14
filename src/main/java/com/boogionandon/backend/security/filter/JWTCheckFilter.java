package com.boogionandon.backend.security.filter;

import com.boogionandon.backend.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

  // TODO : 이건 어디 쓰이는지 확인 필요
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

    // true == not checking

    String path = request.getRequestURI();

    log.info("checking " + path);

    if(path.startsWith("/api/member/") || isMultipartRequest(request)) {
      return true;
    }

    // false == check

    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("............................doFilterInternal....................................");

    if (isMultipartRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Bearer Token 값 전체 : Bearer asdfaswe...
    String authHeaderStr = request.getHeader("Authorization");

    if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Bearer -> 7(띄어쓰기 까지 포함)글자 + JWT 문자열
      String accessToken = authHeaderStr.substring(7);
      log.info("accessToken : " + accessToken);
      Map<String, Object> claims = JWTUtil.validateToken(accessToken);

      log.info("claims : " + claims.toString());


      String username = (String) claims.get("username");
      log.info("username : " + username);

      List<String> authorities = (List<String>) claims.get("roleNames");
      log.info("authorities : " + authorities);

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              username,
              null,
              authorities.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
          );

      log.info("authenticationToken : " + authenticationToken);

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      filterChain.doFilter(request, response);
      //claims를 DTO로 변환하는 작업은 이제 이 필터 이후의 단계, 즉 컨트롤러나 서비스 계층에서 수행해야 합니다.
      //  @GetMapping("/some-endpoint")
      //    public ResponseEntity<?> someEndpoint(Authentication authentication) {
      //        String username = (String) authentication.getPrincipal();
      //        // 여기서 필요에 따라 DTO로 변환
      //        if (username.startsWith("A_") || username.startsWith("S_")) {
      //            AdminDTO adminDTO = convertToAdminDTO(authentication);
      //            // adminDTO를 사용한 로직
      //        } else if (username.startsWith("W_")) {
      //            WorkerDTO workerDTO = convertToWorkerDTO(authentication);
      //            // workerDTO를 사용한 로직
      //        }
      //        // ...
      //    }


// 현재 복잡하게 엮여 있어서 DTO변환 작업을 여기 말고 컨트롤러나 서비스에서 처리 필요
//      if (username.startsWith("A_") || username.startsWith("S_")) {
//        AdminDTO adminDTO = AdminDTO.claimsToDTO(claims);
//
//        log.info("...............");
//        log.info("AdminDTO : " + adminDTO);
//        log.info("adminDTO.getAuthorities() : " + adminDTO.getAuthorities());
//
//
//        // 여기서 adminDTO.getAuthorities() 이건 언제 들어가는거지?
//        // 그리고 들어가는게 저것들이 맞는지
//        UsernamePasswordAuthenticationToken authenticationToken =
//            new UsernamePasswordAuthenticationToken(adminDTO, adminDTO.getPassword(), adminDTO.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//      } else if (username.startsWith("W_")) {
//        log.info("들어는 오나?");
//        WorkerDTO workerDTO = WorkerDTO.claimsToDTO(claims);
//        // TODO : 이 이후로 JWT체크가 안되는 듯, 내일 와서 제일 먼저 확인 필요
//        log.info("나오냐?");
//
//        log.info("...............");
//        log.info("workerDTO : " + workerDTO);
//        log.info("workerDTO.getAuthorities() : " + workerDTO.getAuthorities());
//
//        String password = (String) claims.get("password");
//        log.info("password : " + password);
//
//        // 여기서 adminDTO.getAuthorities() 이건 언제 들어가는거지?
//        UsernamePasswordAuthenticationToken authenticationToken =
//            new UsernamePasswordAuthenticationToken(workerDTO, password, workerDTO.getAuthorities());
//        log.info("authenticationToken : " + authenticationToken);
//        log.info("제발...");
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        log.info("SecurityContextHolder.getContext() : " + SecurityContextHolder.getContext().toString());
//      }
//
//      log.info("건너뛰나?");
//      // dest
//      filterChain.doFilter(request, response);
//      // 여기가 문제인가?
    }catch (Exception e) {
      log.error("JWT Check Error........");
      log.error(e.getMessage());

      log.error("JWT Check Error: " + e.getMessage(), e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Authentication failed: " + e.getMessage());

      Gson gson = new Gson();
      String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

      response.setContentType("application/json");
      PrintWriter printWriter = response.getWriter();
      printWriter.println(msg);
      printWriter.close();
    }
  }

  private boolean isMultipartRequest(HttpServletRequest request) {
    String contentType = request.getContentType();
    return contentType != null && contentType.startsWith("multipart/form-data");
  }
}
