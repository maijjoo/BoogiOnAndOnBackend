package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.PickUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class PickUpLocalServiceImpl implements PickUpService {
    private final PickUpRepository pickUpRepository;
    private final MemberRepository memberRepository;

    @Override
    public void insertPickUp(PickUpRequestDTO pickUpRequestDTO) {
        PickUp pickUp = createPickUpFromDTO(pickUpRequestDTO);
        log.info("---------- : " + pickUp);
        pickUpRepository.save(pickUp);
    }

    private PickUp createPickUpFromDTO(PickUpRequestDTO pickUpRequestDTO) {
        Worker submitter = findSubmitter(pickUpRequestDTO.getSubmitterUsername());

        PickUp pickUp = PickUp.builder()
                .submitter(submitter)
                .pickUpPlace(pickUpRequestDTO.getPickUpPlace())
                .latitude(pickUpRequestDTO.getLatitude())
                .longitude(pickUpRequestDTO.getLongitude())
                .mainTrashType(TrashType.valueOf(pickUpRequestDTO.getMainTrashType()))
                .actualCollectedVolume(pickUpRequestDTO.getActualCollectedVolume())
                .submitDateTime(LocalDateTime.now())
                .build();

        log.info("pick up : " + pickUp.toString());
        addImages(pickUp, pickUpRequestDTO.getUploadedFileNames());
        log.info("여기나와");
        return pickUp;
    }

    private Worker findSubmitter(String submitterUsername) {
        return (Worker) memberRepository.findByUsernameWithDetails(submitterUsername)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + submitterUsername));
    }

    private void addImages(PickUp pickUp, List<String> uploadedFileNames) {
        if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
            uploadedFileNames.forEach((fileName) -> {
                pickUp.addImageString(fileName);
            });
        }
    }
}

