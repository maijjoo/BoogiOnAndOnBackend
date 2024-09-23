package com.boogionandon.backend.advice;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {

  // 아래는 일단 생각나는 에러들 핸들중...
  // 필요한 것이 있으면 추가하기

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> noExist(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> invalidRequest(MethodArgumentNotValidException e) {
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg", e.getMessage()));
  }

//  @ExceptionHandler(CustomJWTException.class)
//  protected ResponseEntity<?> handleJWTException(CustomJWTException e) {
//
//    String msg = e.getMessage();
//
//    return ResponseEntity.ok().body(Map.of("error", msg));
//  }

}
