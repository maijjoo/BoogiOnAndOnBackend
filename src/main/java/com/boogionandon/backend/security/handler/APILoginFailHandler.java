package com.boogionandon.backend.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

// api서버의 경우 잘못됐을 경우 어떻게든 메시지를 전달해야 한다.
// 그래야 받아서 잘못된 것을 받아 처리 할 수 있다.
@Log4j2
public class APILoginFailHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.info("...............APILoginFailHandler.................");

    Gson gson = new Gson();

    String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN"));

    response.setContentType("application/json; charset=UTF-8");

    PrintWriter printWriter = response.getWriter();
    printWriter.println(jsonStr);
    printWriter.close();
  }
}
