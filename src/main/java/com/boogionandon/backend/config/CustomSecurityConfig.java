package com.boogionandon.backend.config;

import com.boogionandon.backend.security.filter.JWTCheckFilter;
import com.boogionandon.backend.security.handler.APILoginFailHandler;
import com.boogionandon.backend.security.handler.APILoginSuccessHandler;
import com.boogionandon.backend.security.handler.CustomAccessDeniedHandler;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    log.info("..............security config...............");

    // CORS(Cross-Origin Resource Sharing) 설정
    // 같은 파일 아래쪽에 만들어 놓음
    http.cors(httpSecurityCorsConfigurer -> {
      httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
    });

    // 리액트로 SPA를 구현하고 API 서버로 스프링 부트를 사용한다면,
    // CSRF 보호가 꼭 필요하지 않을 수 있습니다.
    // SPA에서는 주로 토큰 기반 인증(예: JWT)을 사용하기 때문입니다.
    http.csrf(httpSecurityCsrfConfigurer -> {
      httpSecurityCsrfConfigurer.disable();
    });

    // 로그인 설정, formLogin 이기 때문에 formData 형태로 와야함, Json 아님
    http.formLogin(config -> {
          config
              .loginPage("/api/member/login");
//              .failureUrl("/api/member/login?error=true");  // 필요할까?
          config.successHandler(new APILoginSuccessHandler());
          config.failureHandler(new APILoginFailHandler());
        }
    );

    // 세션 관련 설정 (여기서는 Session을 안만들게 설정)
    http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
      httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
    });

    // 실행전 JWT를 확인하는 설정, 로그인 말고도 다른걸 할때 체크하는지는 알아봐야함
    // TODO : 좀더 어떤 역할을 하는지 알아볼 필요!!
    http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

    // 익셉션 발생시 행동 설정
    http.exceptionHandling(config -> {
      config.accessDeniedHandler(new CustomAccessDeniedHandler());
    });

    // 필요한거 있으면 추가 필요!!!

    return http.build();
  }

  /**
   * 이 메소드는 애플리케이션에 대한 CORS(Cross-Origin Resource Sharing)를 구성합니다.
   * 특정 원본, 메소드 및 헤더를 허용하는 {@link CorsConfigurationSource} Bean을 생성합니다.
   * CORS 구성은 자격 증명을 허용하도록 설정되어 있습니다.
   *
   * @return CORS 구성을 위한 {@link CorsConfigurationSource} Bean
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 허용할 원본 설정 (프로덕션 환경에서는 구체적인 도메인으로 변경해야 함)
    configuration.setAllowedOrigins(Arrays.asList("https://localhost:5173", "http://localhost:5173"));

    // 또는 개발 환경에서 모든 출처를 허용하려면 아래 라인을 사용 (프로덕션에서는 사용하지 않음)
    // configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

    // 허용할 HTTP 메소드 설정
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    // 허용할 헤더 설정
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

    // 브라우저가 접근할 수 있는 헤더 설정
    configuration.setExposedHeaders(Arrays.asList("Authorization"));

    // 자격 증명 허용 (쿠키 등)
    configuration.setAllowCredentials(true);

    // 프리플라이트 요청의 캐시 시간 설정 (1시간)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
