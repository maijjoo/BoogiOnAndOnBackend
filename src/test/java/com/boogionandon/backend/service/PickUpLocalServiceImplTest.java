package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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
        PickUpRequestDTO pickUpRequestDTO = PickUpRequestDTO.builder()
                .submitterUsername("W_testWorker")
                .pickUpPlace("해운대 앞 경찰서")
                .latitude(35.15768265599188)
                .longitude(129.1572648115502)
                .mainTrashType("초목류")
                .actualCollectedVolume(150.0)
                .build();

        pickUpService.insertPickUp(pickUpRequestDTO);
    }




}