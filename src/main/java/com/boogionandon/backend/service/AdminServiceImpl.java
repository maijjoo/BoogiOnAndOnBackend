package com.boogionandon.backend.service;

import com.boogionandon.backend.repository.AdminRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService{

  private final AdminRepository adminRepository;

  @Override
  public List<String> getAssignmentAreaList(Long adminId) {
    List<Object[]> findData = adminRepository.getAssignmentAreaList(adminId);

    return findData.stream().map(data -> (String) data[1]).toList();
  }
}
