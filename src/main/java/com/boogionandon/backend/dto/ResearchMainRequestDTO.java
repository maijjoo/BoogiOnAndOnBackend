package com.boogionandon.backend.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// 예시로 넘어오는 데이터
//   const [researchData, setResearchData] = useState({
//    researcherUsername: '', // 로그인한 사용자의 username // id는 시큐리티에서 내보낼때 수정해야함
//    beachName: '',
//    beachLength: 0,
//    expectedTrashAmount: 0,
//    images: [],
//    weather: '',
//    specialNote: '',
//    subResearches: [
//      {
//        beachNameWithIndex: '',
//        startLatitude: 0,
//        startLongitude: 0,
//        endLatitude: 0,
//        endLongitude: 0,
//        mainTrashType: '',
//      },
//      {
//        beachNameWithIndex: '',
//        startLatitude: 0,
//        startLongitude: 0,
//        endLatitude: 0,
//        endLongitude: 0,
//        mainTrashType: '',
//      }
//    ]

//  });

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchMainRequestDTO {
  // Spring Boot와 Jackson 라이브러리(Spring Boot의 기본 JSON 처리 라이브러리)는 객체 구조를 자동으로 매핑할 수 있습니다.

  // reportTime은 serveice에서 엔티티 변환활때 시간으로 넣을 생각
  // status는 만들어질 때 디폴트로  ReportStatus.ASSIGNMENT_NEEDED; 설정 되어있음

  private Long id; // 필요 없지 않나?
  private String researcherUsername; // 로그인된 정보에서 username을 담아서 넘겨줘야함??
  // dto로 넘어온 beachName을 sevice에서 DTOToEntity로 바꿀때 beachName으로 찾아서 넣어주기
  private String beachName;
  private Double totalBeachLength; // 이 값은 service에서 계산해서 넣을 예정
  private Integer expectedTrashAmount;
  private String weather;
  private String specialNote;

  // 등록/수정용의 진짜 파일들
  @Builder.Default
  private List<MultipartFile> files = new ArrayList<>();

  // 조회용으로 쓸 파일의 이름만 있는 List
  @Builder.Default
  private List<String> uploadedFileNames = new ArrayList<>();

  @Builder.Default
  private List<ResearchSubRequestDTO> researchSubList = new ArrayList<>();

  // 팀원들 ,로 구분할 예정
  @Builder.Default
  private List<String> members = new ArrayList<>();

}
