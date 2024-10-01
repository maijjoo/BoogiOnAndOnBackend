package com.boogionandon.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import com.boogionandon.backend.domain.ResearchSub;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import com.boogionandon.backend.dto.ResearchSubRequestDTO;
import jakarta.persistence.Column;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class ResearchLocalServiceImplTest {

  @Autowired
  private ResearchService researchService;

  @Test
  @DisplayName("insertResearch 메서드 테스트")
  @Commit
  void testInsertResearch() {
    ResearchMainRequestDTO mainDTO = ResearchMainRequestDTO.builder()
        .researcherUsername("W_testWorker")
        .beachName("해운대해수욕장")
        .expectedTrashAmount(180)
        .weather("소나기") // 당일 날씨
        .specialNote("태풍")  // 쓰레기가 영향을 받았을 것 같은 이상기후
        // 이미지는 생략
        .build();

    for (int i = 1; i <= 4; i++) {
      ResearchSubRequestDTO subDTO = ResearchSubRequestDTO.builder()
          .beachNameWithIndex(mainDTO.getBeachName() + i)
          // 반복으로 하니까 위, 경도 바꾸기가 쉽지않아 일단 동일하게 받음
          .startLatitude(35.15768265599188)
          .startLongitude(129.1572648115502)
          .endLatitude(35.15779193363473)
          .endLongitude(129.15770660944662)
          .mainTrashType("폐어구류")
          // researchLength는 실제로 넣을 때는 위의 위,경도를 계산해서 넣기
          .build();

      mainDTO.getResearchSubList().add(subDTO);
    }
    researchService.insertResearch(mainDTO);
  }


}