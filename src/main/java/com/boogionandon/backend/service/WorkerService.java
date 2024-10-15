package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.admin.CreateWorkerRequestDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface WorkerService {

  List<String> findSortedWorkerNameListWithWorkerId(Long workerId);

  void createOneWorker(Long adminId, CreateWorkerRequestDTO createWorkerRequestDTO);

  List<CreateWorkerRequestDTO> exelToDTOList(MultipartFile exel) throws IOException;
}
