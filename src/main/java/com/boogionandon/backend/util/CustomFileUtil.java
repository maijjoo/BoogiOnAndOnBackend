package com.boogionandon.backend.util;

import jakarta.annotation.PostConstruct;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

  @Value("${org.boogi.upload.path}")
  private String uploadPath;

  // 프로젝트가 실행될때 작동하는 메서드
  @PostConstruct
  public void init() {
    File tempFolder = new File(uploadPath);
    if(!tempFolder.exists()) {
      tempFolder.mkdir();
    }

    uploadPath = tempFolder.getAbsolutePath();

    log.info("uploadPath : " + uploadPath);
  }


}
