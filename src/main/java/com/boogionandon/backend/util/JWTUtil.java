package com.boogionandon.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;

/**
 * JWT 토큰을 생성하고 검증하기 위한 JWT 유틸리티 클래스입니다.
 * 이 클래스는 JJWT 라이브러리 버전 0.11.5를 사용합니다.
 */
// 버전(JJWT 0.11.5)에 맞추어서 만든 것이라서 다른 버전을 사용 하면 사용 안됳 수 있음)
@Log4j2
public class JWTUtil {

  /**
   * JWT 토큰 생성 및 검증을 위한 비밀 키입니다.
   * 이 키는 현재 데모용으로 고정 값으로 설정되어 있습니다.
   * 프로덕션 환경에서는 안전한 방법을 사용하여 이 키를 저장하고 검색하는 것을 고려하세요.
   */
  // 키 값이 짧으면 제대로 안될 수 있음, 30자 이상의 긴 문자열 사용 권장
  // application.properties에 저장하고 불러오는 것이 더 좋은 방법
      // TODO : 나중 수정 !! 키가 없다고 나와서 임시로 여기 적어서 사용
//  @Value("${JWTSecretKey}")
  private static String secretKey="1234567890123456789012345678901234567890";
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }


  /**
   * 제공된 클레임 및 만료 시간을 사용하여 JWT 토큰을 생성합니다.
   *
   * @param valueMap JWT 토큰에 포함될 클레임이 포함된 맵입니다.
   * @param min JWT 토큰의 만료 시간(분)입니다.
   * @return 생성된 JWT 토큰을 나타내는 문자열입니다.
   * @throws RuntimeException JWT 토큰 생성 과정에서 오류가 발생한 경우.
   */
  public static String generateToken(Map<String, Object> valueMap, int min) {

    SecretKey key = null;

    try {
      key = Keys.hmacShaKeyFor(JWTUtil.secretKey.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    String jwtStr = Jwts.builder()
        // TODO : 이거 typ으로 끝나는지 type으로 해야 하는지 나중 확인
        .setHeader(Map.of("typ", "JWT"))
        .setClaims(valueMap)
        .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
        .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
        .signWith(key)
        .serializeToJsonWith(new JacksonSerializer(objectMapper))
        .compact();

    return jwtStr;
  }

  /**
   * JWT 토큰의 유효성을 검사하고 해당 클레임을 반환합니다.
   *
   * @param token 유효성을 검사할 JWT 토큰입니다.
   * @return 검증된 JWT 토큰에서 추출된 클레임이 포함된 맵입니다.
   * @throws CustomJWTException JWT 토큰의 형식이 잘못되었거나, 만료되었거나, 유효하지 않거나 유효성 검사 중에 오류가 발생하는 경우입니다.
   */
  public static Map<String, Object> validateToken(String token) {
    Map<String, Object> claims = null;

    try {
     SecretKey key = Keys.hmacShaKeyFor(JWTUtil.secretKey.getBytes(StandardCharsets.UTF_8));

     claims = Jwts.parserBuilder()
         .setSigningKey(key)
         .build()
         .parseClaimsJws(token)
         .getBody();

    } catch(MalformedJwtException malformedJwtException){
      throw new CustomJWTException("MalFormed");
    }catch(ExpiredJwtException expiredJwtException){
      throw new CustomJWTException("Expired");
    }catch(InvalidClaimException invalidClaimException){
      throw new CustomJWTException("Invalid");
    }catch(JwtException jwtException){
      throw new CustomJWTException("JWTError");
    }catch(Exception e){
      throw new CustomJWTException("Error");
    }
    return claims;
  }

}
