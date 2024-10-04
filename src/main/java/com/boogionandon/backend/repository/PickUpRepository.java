package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.PickUp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PickUpRepository extends JpaRepository<PickUp, Long> {

  @Query("SELECT DISTINCT p FROM PickUp p " +
      "LEFT JOIN FETCH p.submitter w " +
      "LEFT JOIN FETCH p.images " +
      "LEFT JOIN FETCH Member a ON w.managerId = a.id " +
      "WHERE a.id = :adminId")
  List<PickUp> findPickUpWithAdminAndImages(@Param("adminId") Long adminId);
}
