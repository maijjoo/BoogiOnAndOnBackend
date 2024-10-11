package com.boogionandon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ResearchSubRequestDTO {

  // 아래 두개는 리퀘스트일때는 쓸일 없을 듯?
  // 리스폰스에서 쓰일지는 아직 몰?루
  private Long id;
  private Long researchId;

  private String beachNameWithIndex;
  private Double startLatitude;
  private Double startLongitude;
  private Double endLatitude;
  private Double endLongitude;

  private String mainTrashType;

  // researchLength는 service에서 DTOToEntity로 변환할때 계산하고 넣기


}
