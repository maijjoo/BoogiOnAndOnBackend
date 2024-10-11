package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.PickUpDetailResponseDTO;
import com.boogionandon.backend.dto.PickUpListForCollectorResponseDTO;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.PickUpRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<PickUpListForCollectorResponseDTO> findPickUpWithAdmin(Long adminId) {

        List<PickUp> findList = pickUpRepository.findPickUpWithAdminAndImages(
            adminId);

        List<PickUpListForCollectorResponseDTO> dtoList = findList.stream()
            .map(pickUp -> {

                return PickUpListForCollectorResponseDTO.builder()
                    .id(pickUp.getId())
                    .submitterName(pickUp.getSubmitter().getName())
                    .pickUpPlace(pickUp.getPickUpPlace())
                    .latitude(pickUp.getLatitude())
                    .longitude(pickUp.getLongitude())
                    .mainTrashType(pickUp.getMainTrashType())
                    .realTrashAmount(pickUp.getRealTrashAmount())
                    .images(pickUp.getImages().stream()
                        .map(image -> "S_" + image.getFileName())
                        .collect(Collectors.toList()))
                    .status(pickUp.getStatus())
                    .lastModifiedBy(pickUp.getLastModifiedBy())
                    .build();
            })
            .collect(Collectors.toList());
        return dtoList;
    }

    @Override
    public void updatePickUpStatusToAddedToRoute(Long pickUpId) {
        // 영속 처리 된것임
        PickUp findPickUp = pickUpRepository.findById(pickUpId)
            .orElseThrow(() -> new EntityNotFoundException("ResearchMain not found with id: " + pickUpId));


        if (ReportStatus.ASSIGNMENT_NEEDED.equals(findPickUp.getStatus())) {
            log.info("상태 변경 시작: {}", findPickUp.getStatus());
            findPickUp.changeStatusToAddedToRoute(ReportStatus.ASSIGNMENT_ADDED_TO_ROUTE);
            log.info("상태 변경 완료: {}", findPickUp.getStatus());
        } else {
            throw new IllegalStateException("Can only change status when current status is ASSIGNMENT_NEEDED");
        }
    }

    @Override
    public void updatePickUpStatusToCompleted(Long pickUpId) {
        // 영속 처리 된것임
        PickUp findPickUp = pickUpRepository.findById(pickUpId)
            .orElseThrow(() -> new EntityNotFoundException("ResearchMain not found with id: " + pickUpId));

        if (ReportStatus.ASSIGNMENT_NEEDED.equals(findPickUp.getStatus()) || ReportStatus.ASSIGNMENT_ADDED_TO_ROUTE.equals(findPickUp.getStatus())) {
            log.info("상태 변경 시작: {}", findPickUp.getStatus());
            findPickUp.changeStatusToCompleted(ReportStatus.ASSIGNMENT_COMPLETED);
            log.info("상태 변경 완료: {}", findPickUp.getStatus());
        } else {
            throw new IllegalStateException("Can only change status when current status is ASSIGNMENT_NEEDED");
        }
    }

    @Override
    public void updatePickUpStatusFromAddedToNeeded(Long pickUpId) {
        // 영속 처리 된것임
        PickUp findPickUp = pickUpRepository.findById(pickUpId)
            .orElseThrow(() -> new EntityNotFoundException("ResearchMain not found with id: " + pickUpId));

        if (ReportStatus.ASSIGNMENT_ADDED_TO_ROUTE.equals(findPickUp.getStatus())) {
            log.info("상태 변경 시작: {}", findPickUp.getStatus());
            findPickUp.changeStatusFromAddedToNeeded(ReportStatus.ASSIGNMENT_NEEDED);
            log.info("상태 변경 완료: {}", findPickUp.getStatus());
        } else {
            throw new IllegalStateException("Can only change status when current status is ASSIGNMENT_NEEDED");
        }
    }

    @Override
    public Page<PickUp> findPickUpByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId) {
        // 수퍼 관리자 인지 아닌지 판별
        // repository에서 결정 할까? 했지만 repository에서 repository를 import하는게 아닌거 같아서 여기서 나눔
        Member admin = memberRepository.findById(adminId)
            .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

        log.info("admin role : " + admin.getMemberRoleList().toString());

        // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
        boolean isContainSuper = admin.getMemberRoleList().stream()
            .anyMatch(role -> role == MemberType.SUPER_ADMIN);

        if (isContainSuper) {
            log.info("SuperAdmin 들어음");
            return pickUpRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
        } else {
            log.info("Admin 들어음");
            return pickUpRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
        }
    }

    @Override
    public PickUpDetailResponseDTO getPickUpDetail(Long pickUpId) {

        PickUp pickUp = pickUpRepository.findByIdWithImage(pickUpId)
            .orElseThrow(() -> new EntityNotFoundException("해당 PickUp을 찾을 수 없습니다. : " + pickUpId));

        return PickUpDetailResponseDTO.builder()
            .id(pickUp.getId())
            .submitterName(pickUp.getSubmitter().getName())
            .pickUpPlace(pickUp.getPickUpPlace())
            .latitude(pickUp.getLatitude())
            .longitude(pickUp.getLongitude())
            .mainTrashType(pickUp.getMainTrashType())
            .submitDateTime(pickUp.getSubmitDateTime())
            .realTrashAmount(pickUp.getRealTrashAmount())
            .status(pickUp.getStatus())
            .images(pickUp.getImages().stream()
                .map(image -> "S_" + image.getFileName())
                .collect(Collectors.toList()))
            .build();
    }


    private PickUp createPickUpFromDTO(PickUpRequestDTO pickUpRequestDTO) {
        Worker submitter = findSubmitter(pickUpRequestDTO.getSubmitterUsername());

        PickUp pickUp = PickUp.builder()
                .submitter(submitter)
                .pickUpPlace(pickUpRequestDTO.getPickUpPlace())
                .latitude(pickUpRequestDTO.getLatitude())
                .longitude(pickUpRequestDTO.getLongitude())
                .mainTrashType(TrashType.valueOf(pickUpRequestDTO.getMainTrashType()))
                .realTrashAmount(pickUpRequestDTO.getRealTrashAmount())
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

