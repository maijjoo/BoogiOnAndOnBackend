package com.boogionandon.backend.util;

public class CustomJWTException extends RuntimeException{

  public CustomJWTException(String message) {
    super(message);
  }

}
