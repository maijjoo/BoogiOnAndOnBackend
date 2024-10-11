package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  Page<Member> findAllByWorkerManagedAdminWithNameSearchForRegular(Long adminId, String tabCondition, String nameSearch, Pageable pageable);
  Page<Member> findAllByWorkerManagedAdminWithNameSearchForSuper(Long adminId, String tabCondition, String nameSearch, Pageable pageable);

}
