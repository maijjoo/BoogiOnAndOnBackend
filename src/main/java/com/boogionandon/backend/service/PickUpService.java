package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.dto.PickUpDetailResponseDTO;
import com.boogionandon.backend.dto.PickUpListForCollectorResponseDTO;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PickUpService {

    public void insertPickUp(PickUpRequestDTO pickUpRequestDTO);

    public List<PickUpListForCollectorResponseDTO> findPickUpWithAdmin(Long adminId);

    void updatePickUpStatusToAddedToRoute(Long pickUpId);

    void updatePickUpStatusToCompleted(Long pickUpId);

    void updatePickUpStatusFromAddedToNeeded(Long pickUpId);

    Page<PickUp> findPickUpByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId);

    PickUpDetailResponseDTO getPickUpDetail(Long pickUpId);
}
