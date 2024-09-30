package com.boogionandon.backend.security.filter;

import com.boogionandon.backend.dto.AdminDTO;
import com.boogionandon.backend.dto.WorkerDTO;
import com.boogionandon.backend.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    if(path.startsWith("/api/member/")) {
      return true;
    }

    // false == check

    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("............................doFilterInternal....................................");

    String authHeaderStr = request.getHeader("Authorization");
    log.info("authHeaderStr : " + authHeaderStr);

    try {
      // Bearer -> 7(띄어쓰기 까지 포함)글자 + JWT 문자열
      String accessToken = authHeaderStr.substring(7);
      Map<String, Object> claims = JWTUtil.validateToken(accessToken);

      log.info("claims : " + claims);

      // username의 앞의 A_, W_ 등으로 파악예정,
      String username = (String) claims.get("username");

      // TODO : 수퍼관리자가 S_ 일경우 추가 필요
      if (username.startsWith("A_") || username.startsWith("S_")) {
        AdminDTO adminDTO = AdminDTO.claimsToDTO(claims);

        log.info("...............");
        log.info("AdminDTO : ", adminDTO);
        log.info("adminDTO.getAuthorities() : ", adminDTO.getAuthorities());


        // 여기서 adminDTO.getAuthorities() 이건 언제 들어가는거지?
        // 그리고 들어가는게 저것들이 맞는지
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(adminDTO, adminDTO.getPassword(), adminDTO.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      } else if (username.startsWith("W_")) {
        WorkerDTO workerDTO = WorkerDTO.claimsToDTO(claims);

        log.info("...............");
        log.info("workerDTO : ", workerDTO);
        log.info("workerDTO.getAuthorities() : ", workerDTO.getAuthorities());

        // 여기서 adminDTO.getAuthorities() 이건 언제 들어가는거지?
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(workerDTO, workerDTO.getPassword(), workerDTO.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }

      // dest
      filterChain.doFilter(request, response);
    }catch (Exception e) {
      log.error("JWT Check Error........");
      log.error(e.getMessage());

      Gson gson = new Gson();
      String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

      response.setContentType("application/json");
      PrintWriter printWriter = response.getWriter();
      printWriter.println(msg);
      printWriter.close();
    }
  }
}
