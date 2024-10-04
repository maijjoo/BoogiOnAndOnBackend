package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.PickUpListForCollectorResponseDTO;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import java.util.List;

public interface PickUpService {

    public void insertPickUp(PickUpRequestDTO pickUpRequestDTO);

    public List<PickUpListForCollectorResponseDTO> findPickUpWithAdmin(Long adminId);

    void updatePickUpStatusToAddedToRoute(Long pickUpId);

    void updatePickUpStatusToCompleted(Long pickUpId);

    void updatePickUpStatusFromAddedToNeeded(Long pickUpId);
}
