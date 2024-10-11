package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Admin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  // admin 마이페이지에서 responseDTO에 담기 위한 sql
  @Query("SELECT DISTINCT a, aal FROM Admin a " +
      "LEFT JOIN a.assignmentAreaList aal " +
      "WHERE a.id = :adminId")
  List<Object[]> getAssignmentAreaList(@Param("adminId") Long adminId);
}
