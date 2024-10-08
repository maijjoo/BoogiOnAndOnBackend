package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.dto.admin.AdminDetailResponseDTO;
import com.boogionandon.backend.dto.admin.WorkerDetailResponseDTO;
import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

  WorkerResponseDTO getWorkerProfile(Long workerId);

  AdminResponseDTO getAdminProfile(Long adminId);

  Page<Member> getMemberByRegularAdmin(Long adminId, String tabCondition, String nameSearch, Pageable pageable);
  Page<Member> getMemberBySuperAdmin(Long adminId, String tabCondition, String nameSearch, Pageable pageable);

  WorkerDetailResponseDTO getWorkerById(Long memberId);

  AdminDetailResponseDTO getAdminById(Long adminId);
}
