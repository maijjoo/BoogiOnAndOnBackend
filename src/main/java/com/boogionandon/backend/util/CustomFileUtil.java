package com.boogionandon.backend.util;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

  // 파일 업로드용
  public List<String> saveFiles(List<MultipartFile> files, String prefix) throws RuntimeException {

    if(files == null || files.size() == 0) {
      return null;
    }

    int count = 1;

    List<String> uploadNames = new ArrayList<>();
    for (MultipartFile file : files) {

      String savedName = prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
          + count +"_" + file.getOriginalFilename();

      Path savePath = Paths.get(uploadPath, savedName);

      try{
        // 원본 파일 업로드
        Files.copy(file.getInputStream(), savePath);

        // 이미지인 경우에만 썸네일(이미지 사이즈 줄인) 만듦
        String contentType = file.getContentType(); // Mime type

        log.info("contentType : " + contentType);

        // 이미지 파일이라면
        if(contentType != null || contentType.startsWith("image")) {

          Path thumbnailPath = Paths.get(uploadPath, "S_" + savedName);

          // Thumbnailator는 일반적으로 WebP 지원안함
          Thumbnails.of(savePath.toFile()).size(200, 200).toFile(thumbnailPath.toFile());
        }

        uploadNames.add(savedName);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return uploadNames;
  }


  // 파일 조회
  // 재사용을 위해 컨트롤러가 아닌 여기에 만듦
  public ResponseEntity<Resource> getFile(String fileName) {

    // File.separator == /
    Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);

    if(!resource.isReadable()) {
      resource = new FileSystemResource(uploadPath+File.separator+"default.png");

    }

    HttpHeaders headers = new HttpHeaders();

    try {
      headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ResponseEntity.ok().headers(headers).body(resource);
  }

}
