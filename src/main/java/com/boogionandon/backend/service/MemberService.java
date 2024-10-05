package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;

public interface MemberService {

  WorkerResponseDTO getWorkerProfile(Long workerId);

  AdminResponseDTO getAdminProfile(Long adminId);
}
