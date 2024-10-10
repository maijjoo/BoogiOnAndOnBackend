package com.boogionandon.backend.service;

import java.util.List;

public interface WorkerService {

  List<String> findSortedWorkerNameListWithWorkerId(Long workerId);

}
