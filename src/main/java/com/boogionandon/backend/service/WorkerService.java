package com.boogionandon.backend.service;

import com.boogionandon.backend.dto.admin.CreateWorkerRequestDTO;
import java.util.List;

public interface WorkerService {

  List<String> findSortedWorkerNameListWithWorkerId(Long workerId);

  void createOneWorker(Long adminId, CreateWorkerRequestDTO createWorkerRequestDTO);
}
