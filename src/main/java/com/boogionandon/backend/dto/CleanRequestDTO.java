package com.boogionandon.backend.dto;

import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanRequestDTO {

  private Long id; // 필요 없지 않나?
  private String cleanerUsername; // 로그인된 정보에서 username을 담아서 넘겨줘야함??
  private String beachName;
  private Integer realTrashAmount; // 실제 쓰레기 양 (ex - 50L쓰레기 봉투를 기준으로 갯수로 계산 예정)

  private Double startLatitude;  //  청소 시작 위치 위도
  private Double startLongitude; // 청소 시작 위치 경도
  private Double endLatitude;  //  청소 끝 위치 위도
  private Double endLongitude; // 청소 끝 위치 경도

  private String mainTrashType; // enum과 동일한 값이 들어와야함

  // 등록/수정용의 진짜 파일들 (before 이미지)
  @Builder.Default
  private List<MultipartFile> beforeFiles = new ArrayList<>();

  // 조회용으로 쓸 파일의 이름만 있는 List (before 이미지의 name)
  @Builder.Default
  private List<String> beforeUploadedFileNames = new ArrayList<>();

  // 등록/수정용의 진짜 파일들 (after 이미지)
  @Builder.Default
  private List<MultipartFile> afterFiles = new ArrayList<>();

  // 조회용으로 쓸 파일의 이름만 있는 List (after 이미지의 name)
  @Builder.Default
  private List<String> afterUploadedFileNames = new ArrayList<>();

  // 팀원들 ,로 구분할 예정
  @Builder.Default
  private List<String> members = new ArrayList<>();

  private String weather;

  private String specialNote;
}
