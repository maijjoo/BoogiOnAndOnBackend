package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.TrashType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Log4j2
class PickUpRepositoryTest {

    @Autowired
    private PickUpRepository pickUpRepository;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("PickUp 추가 테스트")
    @Commit
    void testPickUpInsert() {
        String userName = "W_testWorker";
        Worker findWorker = (Worker) memberRepository.findByUsernameWithDetails(userName)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + userName));


        String PickUpPlace = "해운대 경찰서 앞"; // 집하지명 임의 설정

        PickUp pickup = PickUp.builder()
                .submitter(findWorker)
                .latitude(129.1572648115502)
                .longitude(35.15768265599188)
                .mainTrashType(TrashType.대형_투기쓰레기류)
                .submitDateTime(LocalDateTime.now())
                .actualCollectedVolume(150.0)
                .build();

        // 3. PickUp 엔티티 리포지토리에 저장
        PickUp savedPickup = pickUpRepository.save(pickup);

        // 4. 결과 검증
        assertNotNull(savedPickup);
        assertNotNull(savedPickup.getId());
        log.info("집하지 등록이 성공적으로 저장되었습니다. ID: {}", savedPickup.getId());
    }

}