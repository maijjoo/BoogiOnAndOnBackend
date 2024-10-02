package com.boogionandon.backend.controller;

import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.repository.CleanRepository;
import com.boogionandon.backend.service.CleanService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

  private final CleanService cleanService;

  // 관리자 페이지에서 쓰레기 분포도 볼때 필요한 API
  // 어차피 지도로 표현하는 거니 페이징은 없을것 같아 아래 DTO 만들어씀
  // start가 end 보다 이전 일이게 들어오는건 리액트에서 체크하고 보내야 하지 않을까? 서버쪽에서도 필요한가?
  // 리액트에서 년, 년/월 을 하다가 시작~끝 으로 바뀌거나 그 반대일때 이전 년, 년/월 값을 null로 초기화 해야하고 반대도 마찬가지
  // 4가지 다 들어오면 값을 못찾고 있음, 여기서 수정할 수 있긴 한데 이건 리액트에서 값이 제대로 들어와야 하는 문제가 아닐까?
  @GetMapping("/trash-distribution")
  public TrashMapResponseDTO trashDistribution(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate start,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

    // 매개변수가 제공되지 않으면 현재 연도를 사용합니다.
    if (year == null && month == null && start == null && end == null) {
      year = LocalDate.now().getYear();
    }

    return cleanService.getTrashDistribution(year, month, start, end);
  }

}
