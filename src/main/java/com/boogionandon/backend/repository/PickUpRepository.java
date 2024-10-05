package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.repository.queryDSL.PickUpRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PickUpRepository extends JpaRepository<PickUp, Long>, PickUpRepositoryCustom {

  @Query("SELECT DISTINCT p FROM PickUp p " +
      "LEFT JOIN FETCH p.submitter w " +
      "LEFT JOIN FETCH p.images " +
      "LEFT JOIN FETCH Member a ON w.managerId = a.id " +
      "WHERE a.id = :adminId")
  List<PickUp> findPickUpWithAdminAndImages(@Param("adminId") Long adminId);

  @Query("SELECT distinct p FROM PickUp p "
      + "left join fetch p.submitter s "
      + "left join fetch p.images "
      + "where p.id = :pickUpId" )
  Optional<PickUp> findByIdWithImage(@Param("pickUpId") Long pickUpId);
}
