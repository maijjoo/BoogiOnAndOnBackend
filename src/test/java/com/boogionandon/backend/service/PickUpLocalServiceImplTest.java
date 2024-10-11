package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.PickUpDetailResponseDTO;
import com.boogionandon.backend.dto.PickUpListForCollectorResponseDTO;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import java.util.List;
import java.util.Random;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class PickUpLocalServiceImplTest {
    @Autowired
    private PickUpService pickUpService;

    @Test
    @DisplayName("insertPickUp 메서드 테스트")
    @Commit
    void insertPickUp() {

        Random random = new Random();

        PickUpRequestDTO pickUpRequestDTO = PickUpRequestDTO.builder()
                .submitterUsername("W_testWorker")
                .pickUpPlace("해운대 앞 경찰서")
                .latitude(35.15768265599188)
                .longitude(129.1572648115502)
                .mainTrashType("초목류")
                .realTrashAmount(random.nextInt(1, 10))
                .build();

        pickUpService.insertPickUp(pickUpRequestDTO);
    }

    @Test
    @DisplayName("findPickUpWithAdmin 메서드 테스트")
    void testFindPickUpWithAdmin() {

        // adminId는 기본적으로 5L, 6L, 7L 이 initData에 의해 자동으로 만들어짐
        Long adminId = 5L; // initData에서 자동으로 만든 admin 계정

        List<PickUpListForCollectorResponseDTO> dtoList = pickUpService.findPickUpWithAdmin(adminId);

        log.info("dtoList.size : " + dtoList.size());
        log.info("dtoList : " + dtoList.toString());
    }

    // ----------- findResearchByStatusCompletedAndSearch 테스트 시작 -----------
    @Test
    @DisplayName("findPickUpByStatusCompletedAndSearch 테스트")
    void testFindPickUpByStatusCompletedAndSearch() {

        // super admin -> 1L, 2L, 3L, 4L initData 에서 자동으로 만들어진 super
        // admin -> 5L, 6L, 7L, 8L, 9L initData 에서 자동으로 만들어진 regular
        Long adminId = 1L;

        String beachSearch = "해변";

        // 기본으로 사용
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
            .build();


        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
            pageRequestDTO.getSort().equals("desc") ?
                Sort.by("submitDateTime").descending() :
                Sort.by("submitDateTime").ascending()
        );

    Page<PickUp> findList = pickUpService.findPickUpByStatusCompletedAndSearch(beachSearch, pageable, adminId);
//        Page<PickUp> findList = pickUpService.findPickUpByStatusCompletedAndSearch(null, pageable, adminId);

        log.info("findList : " + findList);
        log.info("findList : " + findList.getContent());
    }
    // ----------- testFindPickUpByStatusCompletedAndSearch 테스트 끝 -----------
    // ----------- getPickUpDetail 테스트 시작 -----------
    @Test
    @DisplayName("getPickUpDetail 메서드 테스트")
    void testGetPickUpDetail() {
        Long pickUpId = 8L;

        PickUpDetailResponseDTO findPickUp = pickUpService.getPickUpDetail(pickUpId);

        log.info("findPickUp : " + findPickUp.toString());
        log.info("findPickUp : " + findPickUp.getImages().toString());
    }
    // ----------- getPickUpDetail 테스트 끝 -----------


}