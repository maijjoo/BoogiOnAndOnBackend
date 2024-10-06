package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.repository.WorkerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class WorkerLocalServiceImpl implements WorkerService{

  private final WorkerRepository workerRepository;

  @Override
  public List<String> findSortedWorkerNameList() {

    List<Worker> all = workerRepository.findAll();

    List<String> nameWithLastFourNumber = all.stream()
        .map(worker -> {
          String name = worker.getName();
          String phone = worker.getPhone();
          if (phone != null) {
            // '-'를 제거하고 숫자만 남김
            String digitsOnly = phone.replaceAll("-", "");
            if (digitsOnly.length() >= 4) {
              return name + " " + digitsOnly.substring(digitsOnly.length() - 4);
            }
          }
          return name + " (전화번호 없음)";
        })
        .collect(Collectors.toList());

    nameWithLastFourNumber.sort(String::compareTo);  // String::compareTo : compareTo()를 구현한 String 클래스를 사용

    log.info("nameWithLastFourNumber " + nameWithLastFourNumber);
    log.info("nameWithLastFourNumber.size() :  " + nameWithLastFourNumber.size());



    return nameWithLastFourNumber;
  }
}
