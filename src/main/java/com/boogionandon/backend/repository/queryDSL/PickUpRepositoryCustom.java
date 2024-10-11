package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.PickUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PickUpRepositoryCustom {

  Page<PickUp> findByStatusCompletedAndSearchForSuper(String beachSearch, Pageable pageable);
  Page<PickUp> findByStatusCompletedAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId);

}
