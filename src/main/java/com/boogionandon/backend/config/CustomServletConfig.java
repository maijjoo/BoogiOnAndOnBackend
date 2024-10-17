package com.boogionandon.backend.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 이 클래스는 Spring MVC 프레임워크를 커스터마이징하기 위한 구성 클래스이다. 사용자 정의를 제공하기 위해 {@link WebMvcConfigurer} 인터페이스를
 * 구현합니다.
 *
 * @author BoogionAndOn
 * @since 1.0.0
 */
@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {
    
    // Add your customizations here.
    // For example, you can override methods from WebMvcConfigurer interface to customize the behavior of Spring MVC.
    
}
