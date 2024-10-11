package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.admin.CreateAdminRequestDTO;
import java.util.List;

public interface AdminService {

  List<String> getAssignmentAreaList(Long adminId);

  void createOneAdmin(Long adminId, CreateAdminRequestDTO createAdminRequestDTO);
}
