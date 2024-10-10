package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.WorkerRepository;
import java.util.ArrayList;
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
  private final MemberRepository memberRepository;

  @Override
  public List<String> findSortedWorkerNameListWithWorkerId(Long workerId) {

    Object[] byIdWithManager = memberRepository.findByIdWithManager(workerId)
        .orElseThrow(() -> new RuntimeException("Member not found with WorkerId : " + workerId));

    Long adminId = null;
    if (byIdWithManager.length > 0 && byIdWithManager[0] instanceof Object[]) {
      Object[] innerArray = (Object[]) byIdWithManager[0];
      if (innerArray.length >= 2 && innerArray[1] instanceof Admin) {
        adminId = ((Admin) innerArray[1]).getId();
      } else {
        log.error("Admin not found with WorkerId : " + workerId);
      }
    }else {
      // WorkerId로 Member 찾기 실패
      log.error("findSortedBeachNameListWithWorkerId - Member not found with WorkerId : " + workerId);
      return new ArrayList<>();
    }

    List<Worker> allBySameAdmin = workerRepository.getAllBySameAdmin(adminId);

    List<String> nameWithLastFourNumber = allBySameAdmin.stream()
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
