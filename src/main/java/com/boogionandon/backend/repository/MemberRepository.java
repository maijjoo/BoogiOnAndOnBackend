package com.boogionandon.backend.repository;

import com.boogionandon.backend.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  @EntityGraph(attributePaths = {"memberRoleList"})
  @Query("select m from Member m LEFT JOIN FETCH Admin a on m.id = a.id LEFT JOIN FETCH Worker w on m.id = w.id WHERE m.username = :username")
  Optional<Member> findByUsernameWithDetails(@Param("username") String username);

//  @EntityGraph(attributePaths = {"memberRoleList"})
//  @Query("select a from Admin a where a.username = :username")
//  Optional<Admin> getAdminWithRoles(@Param("username") String username);
//
//  @EntityGraph(attributePaths = {"memberRoleList"})
//  @Query("select w from Worker w where w.username = :username")
//  Optional<Worker> getWorkerWithRoles(@Param("username") String username);

}
