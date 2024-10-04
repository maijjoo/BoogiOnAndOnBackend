package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.repository.queryDSL.CleanRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleanRepository extends JpaRepository<Clean, Long>, CleanRepositoryCustom {

  @Query("select distinct c from Clean c "
      + "left join fetch c.cleaner cr "
      + "left join fetch c.images "
      + "where c.id = :cleanId")
  Optional<Clean> findByIdWithImage(@Param("cleanId") Long cleanId);

}
