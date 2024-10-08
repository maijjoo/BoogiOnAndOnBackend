package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.repository.queryDSL.MemberRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  @EntityGraph(attributePaths = {"memberRoleList"})
  @Query("select m from Member m "
      + "LEFT JOIN FETCH Admin a on m.id = a.id "
      + "LEFT JOIN FETCH Worker w on m.id = w.id "
      + "WHERE m.username = :username")
  Optional<Member> findByUsernameWithDetails(@Param("username") String username);


  @Query("select m, a from Member m "
      + "LEFT JOIN FETCH Admin a on m.managerId = a.id "
      + "LEFT JOIN FETCH Worker w on m.id = w.id "
      + "WHERE m.id = :workerId")
  Optional<Object> findByIdWithManager(@Param("workerId") Long workerId);
}
