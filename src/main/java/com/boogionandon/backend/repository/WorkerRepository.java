package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Worker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

  @Query("select w from Worker w "
      + "where w.managerId = :adminId")
  List<Worker> getAllBySameAdmin(@Param("adminId") Long adminId);
}
