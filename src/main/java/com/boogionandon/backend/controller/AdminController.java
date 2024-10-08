package com.boogionandon.backend.controller;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Image;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.dto.CleanDetailResponseDTO;
import com.boogionandon.backend.dto.CleanListResponseDTO;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.boogionandon.backend.dto.PageResponseDTO;
import com.boogionandon.backend.dto.PickUpDetailResponseDTO;
import com.boogionandon.backend.dto.PickUpListResponseDTO;
import com.boogionandon.backend.dto.ResearchMainDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchMainListResponseDTO;
import com.boogionandon.backend.dto.admin.AdminDetailResponseDTO;
import com.boogionandon.backend.dto.admin.BasicStatisticsResponseDTO;
import com.boogionandon.backend.dto.admin.MemberInquiryPageForRegularResponseDTO;
import com.boogionandon.backend.dto.admin.MemberInquiryPageForSuperResponseDTO;
import com.boogionandon.backend.dto.admin.PredictionResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.dto.admin.WorkerDetailResponseDTO;
import com.boogionandon.backend.repository.AdminRepository;
import com.boogionandon.backend.service.BeachService;
import com.boogionandon.backend.service.CleanService;
import com.boogionandon.backend.service.MemberService;
import com.boogionandon.backend.service.PickUpService;
import com.boogionandon.backend.service.ResearchLocalServiceImpl;
import com.boogionandon.backend.service.ResearchService;
import com.boogionandon.backend.util.CustomFileUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final ResearchService researchService;
  private final PickUpService pickUpService;
  private final CustomFileUtil fileUtil;
  private final AdminRepository adminRepository;
  private final MemberService memberService;

  // 리팩토링은 나중에 여유 있을때 하기
  // 1. trashDistribution, collectPrediction 메서드에도 tabCondition이 있었으면 좀더 명확하게 구분이 되었을텐데
  // 2. 시간이 부족해서 일단 만들고 보자 해서 중복 코드들이 많이 발생한거 같음


  // ------------ 데이터 관리 탭 시작 ----------------------

  // 관리자 페이지에서 쓰레기 분포도 볼때 필요한 API
  // 어차피 지도로 표현하는 거니 페이징은 없을것 같아 아래 DTO 만들어씀
  // start가 end 보다 이전 일이게 들어오는건 리액트에서 체크하고 보내야 하지 않을까? 서버쪽에서도 필요한가?
  // 리액트에서 년, 년/월 을 하다가 시작~끝 으로 바뀌거나 그 반대일때 이전 년, 년/월 값을 null로 초기화 해야하고 반대도 마찬가지
  // 4가지 다 들어오면 값을 못찾고 있음, 여기서 수정할 수 있긴 한데 이건 리액트에서 값이 제대로 들어와야 하는 문제가 아닐까?
  // 관리자 상관없이 모든 데이터 다 볼 수 있음
  // TODO : tabCondition으로 구분을 안해서 현재 값이 4개가 다 들어 오면 값에 에러가 생김, 리액트에서 잘 보내주면 상관 없지만, 이런건 사전에 서버에서 차단해야 하는게 아닐까? - 파라미터랑 리턴을 건드리지 않으면서 해결할 수 있는 방법 찾아보고 리팩토링 하기
  @GetMapping("/trash-distribution")
  public List<TrashMapResponseDTO> trashDistribution(
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

  // 관리자 페이지에서 수거 예측량 분포도 볼때 필요한 API - 조사 테이블을 활용해서 만드는 분포도 trashDistribution와 비슷하게
  // TODO : tabCondition으로 구분을 안해서 현재 값이 4개가 다 들어 오면 값에 에러가 생김, 리액트에서 잘 보내주면 상관 없지만, 이런건 사전에 서버에서 차단해야 하는게 아닐까? - 파라미터랑 리턴을 건드리지 않으면서 해결할 수 있는 방법 찾아보고 리팩토링 하기
  @GetMapping("/collect-prediction") // collect이지만 research 테이블들을 바탕으로 조사를 전체적으로 보여준 것임
  public List<PredictionResponseDTO> collectPrediction(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate start,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

    // 매개변수가 제공되지 않으면 현재 연도를 사용합니다.
    if (year == null && month == null && start == null && end == null) {
      year = LocalDate.now().getYear();
    }

    return researchService.getCollectPrediction(year, month, start, end);

  }



  // 관리자 페이지에서 기초 통계 볼때 필요한 API
  // 연도별, 월별, 일별을 탭으로 표현한다고 했는데, 그래서 하나의 주소로 받음
  // 내려줄때 시군구, beachName은 넣어 줘야 하나?
  // 관리자 상관없이 모든 데이터 다 볼 수 있음
  @GetMapping("/basic-statistics")
  public BasicStatisticsResponseDTO basicStatistics(
      @RequestParam String tapCondition,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) String beachName) {

    BasicStatisticsResponseDTO findBasicStatistics = cleanService.getBasicStatistics(tapCondition, year, month, beachName);

    List<String> guGunList = beachService.SortedGuGunList();
    Map<String, List<String>> beachNameMap = beachService.SortedBeachNameMap();

    findBasicStatistics.setGuGun(guGunList);
    findBasicStatistics.setBeachName(beachNameMap);

    return findBasicStatistics;
  }

  // ------------ 데이터 관리 탭 끝 ----------------------

  // ------------ 작업 관리 탭 시작 ----------------------

  // 관리자 페이지에서 NEW-작업 에서 보이는 화면
  // 작업한 Worker의 관리자의 내용만 보여져야함
  @GetMapping("/new-tasks/{adminId}")
  public PageResponseDTO<?> getNewTaskList(String tabCondition, String beachSearch, PageRequestDTO pageRequestDTO, @PathVariable("adminId") Long adminId) {

    if (tabCondition.equals("조사 완료")) {

      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").ascending());
      }

      Page<ResearchMain> findList = researchLocalServiceImpl.findResearchByStatusNeededAndSearch(beachSearch, pageable, adminId);



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

      Page<Clean> findList = cleanService.findResearchByStatusNeededAndSearch(beachSearch, pageable, adminId);

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

  @GetMapping("/new-tasks/research/{researchId}")
  public ResearchMainDetailResponseDTO getNewTasksByResearch(@PathVariable("researchId") Long researchId) {

    return researchService.getResearchDetail(researchId);
  }

  // 한번 배정하면 취소 안됨!!
  @PatchMapping("/new-tasks/research/completed/{researchId}")
  public Map<String,String> researchUpdateStatusToCompleted(@PathVariable("researchId") Long researchId) {
    researchService.updateStatus(researchId);
    return Map.of("message", "success");
  }

  @GetMapping("/new-tasks/clean/{cleanId}")
  public CleanDetailResponseDTO getNewTasksByClean(@PathVariable("cleanId") Long cleanId) {
    return cleanService.getCleanDetail(cleanId);
  }

  // 한번 배정하면 취소 안됨!!
  @PatchMapping("/new-tasks/clean/completed/{cleanId}")
  public Map<String,String> cleanUpdateStatusToCompleted(@PathVariable("cleanId") Long cleanId) {
    cleanService.updateStatus(cleanId);
    return Map.of("message", "success");
  }

  // 관리자 페이지에서 작업 조회(completed-작업) 에서 보이는 화면
  // 작업한 Worker의 관리자의 내용만 보여져야함
  @GetMapping("/completed-tasks/{adminId}")
  public PageResponseDTO<?> getCompletedTaskList(String tabCondition, String beachSearch, PageRequestDTO pageRequestDTO, @PathVariable("adminId") Long adminId) {

    if (tabCondition.equals("조사")) {

      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("reportTime").ascending());
      }

      Page<ResearchMain> findList = researchLocalServiceImpl.findResearchByStatusCompletedAndSearch(beachSearch, pageable, adminId);

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
    } else if (tabCondition.equals("청소")) {
      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("cleanDateTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("cleanDateTime").ascending());
      }

      Page<Clean> findList = cleanService.findResearchByStatusCompletedAndSearch(beachSearch, pageable, adminId);

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
    } else if (tabCondition.equals("수거")) {

      Pageable pageable = null;
      if (pageRequestDTO.getSort().equals("desc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("submitDateTime").descending());
      } else if (pageRequestDTO.getSort().equals("asc")) {
        pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("submitDateTime").ascending());
      }

      Page<PickUp> findList = pickUpService.findPickUpByStatusCompletedAndSearch(beachSearch, pageable, adminId);

      List<PickUpListResponseDTO> responseDTOList = findList.stream().map(pickUp -> {
        return PickUpListResponseDTO.builder()
            .id(pickUp.getId())
            .submitterName(pickUp.getSubmitter().getName())
            .pickUpPlace(pickUp.getPickUpPlace())
            .latitude(pickUp.getLatitude())
            .longitude(pickUp.getLongitude())
            .mainTrashType(pickUp.getMainTrashType())
            .submitDateTime(pickUp.getSubmitDateTime())
            .realTrashAmount(pickUp.getRealTrashAmount())
            .status(pickUp.getStatus())
            .thumbnail(pickUp.getImages().stream()
                .min(Comparator.comparing(Image::getOrd))
                .map(image -> "S_" + image.getFileName())
                .orElse(null))
            .build();
      }).collect(Collectors.toList());
      return new PageResponseDTO(responseDTOList, pageRequestDTO, findList.getTotalElements());
    }

    return null;
  }

  // completed-tasks에서 List를 보낼때 어차피 COMPLETED 들만 나오니 추가로 거를 필요 없음
  @GetMapping("/completed-tasks/research/{researchId}")
  public ResearchMainDetailResponseDTO getCompletedTasksByResearch(@PathVariable("researchId") Long researchId) {
    return researchService.getResearchDetail(researchId);
  }
  @GetMapping("/completed-tasks/clean/{cleanId}")
  public CleanDetailResponseDTO getCompletedTasksByClean(@PathVariable("cleanId") Long cleanId) {
    return cleanService.getCleanDetail(cleanId);
  }
  @GetMapping("/completed-tasks/pick-up/{pickUpId}")
  public PickUpDetailResponseDTO getCompletedTasksByPickUp(@PathVariable("pickUpId") Long pickUpId) {
    return pickUpService.getPickUpDetail(pickUpId);
  }

  @GetMapping("/view/{fileName}")
  public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
    return fileUtil.getFile(fileName);
  }

  // ------------ 작업 관리 탭 끝 ----------------------
  // ------------ 회원 관리 탭 시작 ----------------------

  // 회원 조회에 관련된 메서드
  @GetMapping("/member-inquiry/{adminId}")  // 여기서 adminId는 로그인한 adminId
  public PageResponseDTO<?> getMemberInquiryList(
      @PathVariable("adminId") Long adminId,
      @RequestParam String tabCondition,
      PageRequestDTO pageRequestDTO,
      String nameSearch) {

    Admin admin = adminRepository.findById(adminId)
        .orElseThrow(() -> new IllegalArgumentException("해당 adminId로 Admin을 찾을 수 없습니다. : " + adminId));

    List<MemberType> memberRoleList = admin.getMemberRoleList();
    boolean isSuper = memberRoleList.contains(MemberType.SUPER_ADMIN);

    pageRequestDTO.setSize(20); // 이 페이지에서는 기본으로 20으로 쓰기 위해

    Pageable pageable = null;
    if (pageRequestDTO.getSort().equals("desc")) {
      pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("createdDate").descending());
    } else if (pageRequestDTO.getSort().equals("asc")) {
      pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by("createdDate").ascending());
    }

    List<MemberInquiryPageForSuperResponseDTO> superResponseDTOList = null;

    if (isSuper) {
      Page<Member> findList = memberService.getMemberBySuperAdmin(adminId, tabCondition, nameSearch, pageable);
        if ("관리자".equals(tabCondition)) {
          superResponseDTOList = findList.stream().map(member -> {
            Admin memberToAdmin = (Admin) member;
            return MemberInquiryPageForSuperResponseDTO.builder()
              .id(memberToAdmin.getId())
              .name(memberToAdmin.getName())
              .role("ADMIN")
              .workPlace(memberToAdmin.getWorkPlace())
              .department(memberToAdmin.getDepartment())
              .contact(memberToAdmin.getContact())
              .email(memberToAdmin.getEmail())
              .build();
          }).collect(Collectors.toList());
        } else if ("조사/청소".equals(tabCondition) || "수거자".equals(tabCondition)) {
          superResponseDTOList = findList.stream().map(member -> {
            Worker memberToWorker = (Worker) member;
            return MemberInquiryPageForSuperResponseDTO.builder()
                .id(memberToWorker.getId())
                .name(memberToWorker.getName())
                .role("WORKER")
                .phone(memberToWorker.getPhone())
                .vehicleCapacity(memberToWorker.getVehicleCapacity())
                .createdDate(memberToWorker.getCreatedDate())
                .startDate(memberToWorker.getStartDate())
                .endDate(memberToWorker.getEndDate())
                .build();
          }).collect(Collectors.toList());
        }

      return new PageResponseDTO(superResponseDTOList, pageRequestDTO, findList.getTotalElements());
    } else {
      Page<Member> findList = memberService.getMemberByRegularAdmin(adminId, tabCondition, nameSearch, pageable);

        List<MemberInquiryPageForRegularResponseDTO> regularResponseDTOList = findList.stream().map(member -> {
          Worker worker = (Worker) member;
          return MemberInquiryPageForRegularResponseDTO.builder()
              .id(member.getId())
              .name(member.getName())
              .phone(member.getPhone())
              .createdDate(member.getCreatedDate())
              .vehicleCapacity(worker.getVehicleCapacity())
              .startDate(worker.getStartDate())
              .endDate(worker.getEndDate())
              .build();
      }).collect(Collectors.toList());

      return new PageResponseDTO(regularResponseDTOList, pageRequestDTO, findList.getTotalElements());
    }
  }

  // 회원 상세에 관련된 메서드
  @GetMapping("/member-inquiry/worker/{workerId}") // 여기서 workerId는 상세보기할 것의 id
  public WorkerDetailResponseDTO getWorkerDetail(@PathVariable("workerId") Long workerId) {
    return memberService.getWorkerById(workerId);
  }

  @GetMapping("/member-inquiry/admin/{adminId}") // 여기서 adminId는 상세보기할 것의 id
  public AdminDetailResponseDTO getAdminDetail(@PathVariable("adminId") Long adminId) {
    return memberService.getAdminById(adminId);
  }

  // ------------ 작업 관리 탭 끝 ----------------------



}
