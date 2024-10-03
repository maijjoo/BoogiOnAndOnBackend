package com.boogionandon.backend.controller;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Image;
import com.boogionandon.backend.domain.QImage;
import com.boogionandon.backend.domain.QResearchMain;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.dto.CleanListResponseDTO;
import com.boogionandon.backend.dto.CleanResponseDTO;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.PageResponseDTO;
import com.boogionandon.backend.dto.ResearchMainListResponseDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.CleanRepository;
import com.boogionandon.backend.service.BeachService;
import com.boogionandon.backend.service.CleanService;
import com.boogionandon.backend.service.ResearchLocalServiceImpl;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  private final BeachService beachService;
  private final ResearchLocalServiceImpl researchLocalServiceImpl;
  private final JPAQueryFactory queryFactory;


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

  // 관리자 페이지에서 기초 통계 볼때 필요한 API
  // 연도별, 월별, 일별을 탭으로 표현한다고 했는데, 그래서 하나의 주소로 받음
  // 내려줄때 시군구, beachName은 넣어 줘야 하나?
  @GetMapping("/basic-statistics")
  public BasicStatisticsResponseDTO basicStatistics(
      @RequestParam String tapCondition,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) String beachName) {

    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, month, beachName);

    Set<String> guGunSet = beachService.guGunSet();
    Map<String, List<String>> beachNameMap = beachService.beachNameMap();

    findBasicStatistics.setGuGun(guGunSet);
    findBasicStatistics.setBeachName(beachNameMap);

    return findBasicStatistics;
  }

  // 관리자 페이지에서 new-작업 에서 보이는 화면
  @GetMapping("/new-tasks")
  public PageResponseDTO getNewTask(String tabCondition, String beachSearch, PageRequestDTO pageRequestDTO) {

    if (tabCondition.equals("조사 완료")) {

      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").ascending());
      }

      Page<ResearchMain> findList = researchLocalServiceImpl.findResearchByStatusNeededAndSearch(beachSearch, pageable);



      List<ResearchMainListResponseDTO> responseDTOList = findList.stream().map(research -> {
        return ResearchMainListResponseDTO.builder()
            .id(research.getId())
            .beachName(research.getBeach().getBeachName())
            .researcherName(research.getResearcher().getName())
            .totalBeachLength(research.getTotalBeachLength())
            .expectedTrashAmount(research.getExpectedTrashAmount())
            .reportTime(research.getReportTime())
            .status(research.getStatus())
            .weather(research.getWeather())
            .specialNote(research.getSpecialNote())
            .thumbnail(research.getImages().stream()
                .min(Comparator.comparing(Image::getOrd))
                .map(image -> "S_" + image.getFileName())
                .orElse(null))
            .build();
      }).collect(Collectors.toList());
      return new PageResponseDTO(responseDTOList, pageRequestDTO, findList.getTotalElements());
    } else if (tabCondition.equals("청소 완료")) {

      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("cleanDateTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("cleanDateTime").ascending());
      }

      Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(beachSearch, pageable);
      List<CleanListResponseDTO> responseDTOList = findList.stream().map(clean -> {
        return CleanListResponseDTO.builder()
            .id(clean.getId())
            .beachName(clean.getBeach().getBeachName())
            .cleanerName(clean.getCleaner().getName())
            .realTrashAmount(clean.getRealTrashAmount())
            .cleanDateTime(clean.getCleanDateTime())
            .beachLength(clean.getBeachLength())
            .mainTrashType(clean.getMainTrashType())
            .status(clean.getStatus())
            .thumbnail(clean.getImages() != null && !clean.getImages().isEmpty()
                ? "S_" + clean.getImages().get(0).getFileName()
                : null)
            .build();
      }).collect(Collectors.toList());
      return new PageResponseDTO(responseDTOList, pageRequestDTO, findList.getTotalElements());
    }
    return null;
  }

}
