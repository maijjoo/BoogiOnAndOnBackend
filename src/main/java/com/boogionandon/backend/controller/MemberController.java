package com.boogionandon.backend.controller;

import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;
import com.boogionandon.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;


  @GetMapping("/my-page/worker/{workerId}")
  public WorkerResponseDTO getWorkerProfile(@PathVariable("workerId") Long workerId) {
    log.info("workerId: " + workerId);
    return memberService.getWorkerProfile(workerId);
  }

  @GetMapping("/my-page/admin/{adminId}")
  public AdminResponseDTO getAdminProfile(@PathVariable("adminId") Long adminId) {
    log.info("adminId: " + adminId);
    return memberService.getAdminProfile(adminId);
  }

}
