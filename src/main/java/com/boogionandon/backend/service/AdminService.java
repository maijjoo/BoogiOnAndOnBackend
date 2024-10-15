package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.admin.CreateAdminRequestDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

  List<String> getAssignmentAreaList(Long adminId);

  void createOneAdmin(Long adminId, CreateAdminRequestDTO createAdminRequestDTO);

  List<CreateAdminRequestDTO> exelToDTOList(MultipartFile exel) throws IOException;
}
