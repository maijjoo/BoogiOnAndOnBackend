package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.ResearchSub;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.ResearchMainDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import com.boogionandon.backend.dto.ResearchSubDetailResponseDTO;
import com.boogionandon.backend.dto.ResearchSubRequestDTO;
import com.boogionandon.backend.dto.admin.PredictionResponseDTO;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.repository.ResearchMainRepository;
import com.boogionandon.backend.repository.ResearchSubRepository;
import com.boogionandon.backend.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ResearchLocalServiceImpl implements ResearchService{
  // ResearchLocalServiceImpl 에서 local은 프로젝트내에 파일들이 저장되기 때문
  // AWS 같은거 쓰면 aws라고 바꿀 예정

  private final BeachRepository beachRepository;
  private final MemberRepository memberRepository;
  private final ResearchMainRepository researchMainRepository;
  private final ResearchSubRepository researchSubRepository;

  @Override
  public void insertResearch(ResearchMainRequestDTO mainDTO) {
    // 하다보니 함수 안에서 save처리까지 했음
//    ResearchMain researchMain = dtoToEntity(mainDTO);

    // 아래코드가 에러나면 위의 코드 부활시키기
    //   @OneToMany(mappedBy = "research", cascade = CascadeType.ALL, orphanRemoval = true)
    //  @Builder.Default
    //  private List<ResearchSub> researchSubList = new ArrayList<>();
    // 이기 때문에 안에 들어 있는 sub가 자동으로 들어갈 것이라고 생각중
    ResearchMain researchMain = createResearchMainFromDTO(mainDTO);

    // sub의 List들에서 각각 beachLength를 가져와서 total에 더해주는 작업
    List<ResearchSub> researchSubList = researchMain.getResearchSubList();
    Double totalBeachLength = 0.0;
    for(ResearchSub researchSub : researchSubList) {
      totalBeachLength += researchSub.getResearchLength();
    }
    log.info("TotalBeachLength: " + totalBeachLength);

    researchMain.setTotalResearch(totalBeachLength);

    researchMainRepository.save(researchMain);
  }

  @Override
  public void updateStatus(Long id) {

    // 영속 처리 된것임
    ResearchMain findResearchMain = researchMainRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("ResearchMain not found with id: " + id));


    if (ReportStatus.ASSIGNMENT_NEEDED.equals(findResearchMain.getStatus())) {
      log.info("상태 변경 시작: {}", findResearchMain.getStatus());
      findResearchMain.changeStatusToCompleted(ReportStatus.ASSIGNMENT_COMPLETED);
      log.info("상태 변경 완료: {}", findResearchMain.getStatus());
    } else {
      throw new IllegalStateException("Can only change status when current status is ASSIGNMENT_NEEDED");
    }
  }

  @Override
  public Page<ResearchMain> findResearchByStatusNeededAndSearch(String beachSearch, Pageable pageable, Long adminId) {
    // tapCondition은 컨트롤러에서 처리하기

    // 수퍼 관리자 인지 아닌지 판별
    // repository에서 결정 할까? 했지만 repository에서 repository를 import하는게 아닌거 같아서 여기서 나눔
    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
      return researchMainRepository.findByStatusNeededAndSearchForSuper(beachSearch, pageable);
    } else {
      log.info("Admin 들어음");
      return researchMainRepository.findByStatusNeededAndSearchForRegular(beachSearch, pageable, adminId);
    }
  }

  @Override
  public Page<ResearchMain> findResearchByStatusCompletedAndSearch(String beachSearch, Pageable pageable, Long adminId) {
    // tapCondition은 컨트롤러에서 처리하기

    // 수퍼 관리자 인지 아닌지 판별
    // repository에서 결정 할까? 했지만 repository에서 repository를 import하는게 아닌거 같아서 여기서 나눔
    Member admin = memberRepository.findById(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    log.info("admin role : " + admin.getMemberRoleList().toString());

    // size나 0번째 같은 경우에는 에러가 날 수 있다고 생각해서 아래처럼 만듦
    boolean isContainSuper = admin.getMemberRoleList().stream()
        .anyMatch(role -> role == MemberType.SUPER_ADMIN);

    if (isContainSuper) {
      log.info("SuperAdmin 들어음");
      return researchMainRepository.findByStatusCompletedAndSearchForSuper(beachSearch, pageable);
    } else {
      log.info("Admin 들어음");
      return researchMainRepository.findByStatusCompletedAndSearchForRegular(beachSearch, pageable, adminId);
    }
  }

  @Override
  public ResearchMainDetailResponseDTO getResearchDetail(Long researchId) {

    ResearchMain findMain = researchMainRepository.findByIdWithOutSub(researchId)
        .orElseThrow(() -> new EntityNotFoundException("해당 Research를 찾을 수 없습니다. : " + researchId));

    List<ResearchSub> findSubList = researchSubRepository.findListByMainId(researchId);

    String members = findMain.getMembers();

    List<String> memberList = Arrays.stream(members.split(",")).toList();

    return ResearchMainDetailResponseDTO.builder()
        .id(findMain.getId())
        .researcherName(findMain.getResearcher().getName())
        .beachName(findMain.getBeach().getBeachName())
        .totalBeachLength(findMain.getTotalBeachLength())
        .expectedTrashAmount(findMain.getExpectedTrashAmount())
        .reportTime(findMain.getReportTime())
        .status(findMain.getStatus())
        .weather(findMain.getWeather())
        .specialNote(findMain.getSpecialNote())
        .images(findMain.getImages().stream().map(image -> {
          return "S_" + image.getFileName();
        }).collect(Collectors.toList()))
        .researchSubList(findSubList.stream().map(sub -> {
          return ResearchSubDetailResponseDTO.builder()
              .id(sub.getId())
              .beachNameWithIndex(sub.getBeachNameWithIndex())
              .startLatitude(sub.getStartLatitude())
              .startLongitude(sub.getStartLongitude())
              .endLatitude(sub.getEndLatitude())
              .endLongitude(sub.getEndLongitude())
              .mainTrashType(sub.getMainTrashType())
              .researchLength(sub.getResearchLength())
              .build();
        }).collect(Collectors.toList()))
        .members(memberList)
        .build();
  }

  // 수거 예측분석에 필요한 메서드
  @Override
  public List<PredictionResponseDTO> getCollectPrediction(Integer year, Integer month, LocalDate start, LocalDate end) {
    List<ResearchMain> findList = researchMainRepository.findByDateCriteria(year, month, start, end);

    return findList.stream().map(main -> {
      return PredictionResponseDTO.builder()
          .id(main.getId())
          .researcherName(main.getResearcher().getName())
          .beachName(main.getBeach().getBeachName())
          .expectedTrashAmount(main.getExpectedTrashAmount())
          .reportTime(main.getReportTime())
          .fixedLatitude(main.getBeach().getLatitude())
          .fixedLongitude(main.getBeach().getLongitude())
          .build();
    }).collect(Collectors.toList());

  }


  private ResearchMain createResearchMainFromDTO(ResearchMainRequestDTO mainDTO) {
    // 필요한 researcher, beach를 찾고
    Worker researcher = findResearcher(mainDTO.getResearcherUsername());
    Beach beach = findBeach(mainDTO.getBeachName());

    // List로 받은 팀원을 ,로 한번에 넣어 버리기
    String members = listToStringMembers(mainDTO.getMembers());

    // DTO에서 받은 값으로 ResearchMain 생성
    ResearchMain researchMain = ResearchMain.builder()
        .researcher(researcher)
        .beach(beach)
        .totalBeachLength(mainDTO.getTotalBeachLength())
        .expectedTrashAmount(mainDTO.getExpectedTrashAmount())
        .reportTime(LocalDateTime.now())
        .weather(mainDTO.getWeather())
        .specialNote(mainDTO.getSpecialNote())
        .members(members)
        .build();

    // 빌더로 하기에는 까다로운 부분을 추가로 설정
    addSubResearches(researchMain, mainDTO.getResearchSubList());
    addImages(researchMain, mainDTO.getUploadedFileNames());
    return researchMain;
  }



  private Worker findResearcher(String researcherName) {
    return (Worker) memberRepository.findByUsernameWithDetails(researcherName)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + researcherName));
  }

  private Beach findBeach(String beachName) {
    return beachRepository.findById(beachName)
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));
  }

  private void addSubResearches(ResearchMain researchMain, List<ResearchSubRequestDTO> subList) {
    if (subList != null) {
      for (ResearchSubRequestDTO subDTO : subList) {
        ResearchSub researchSub = ResearchSub.builder()
            // 리액트에서 처리해서 넘어 오기로 했기 때문에 beachNameWithIndex 따로 건드릴 필요 없음
            .beachNameWithIndex(subDTO.getBeachNameWithIndex())
            .startLatitude(subDTO.getStartLatitude())
            .startLongitude(subDTO.getStartLongitude())
            .endLatitude(subDTO.getEndLatitude())
            .endLongitude(subDTO.getEndLongitude())
            .mainTrashType(TrashType.valueOf(subDTO.getMainTrashType()))
            // 받은 위경도를 아래 클래스를 통해 계산해서 바로 넣음
            .researchLength(DistanceCalculator.calculateDistance(
                subDTO.getStartLatitude(), subDTO.getStartLongitude(),
                subDTO.getEndLatitude(), subDTO.getEndLongitude()
            ))
            .build();
        researchMain.addResearchSubList(researchSub);
      }
    }
  }

  private void addImages(ResearchMain researchMain, List<String> uploadedFileNames) {
    if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
      uploadedFileNames.forEach((fileName) -> {
      researchMain.addImageString(fileName);
    });
    }
  }
  private String listToStringMembers(List<String> members) {

    String result = "";
    if(members != null && !members.isEmpty()) {
      for (int i=0; i< members.size(); i++) {
        if ((members.size() -1) == i) {
          result += members.get(i);
        } else {
          result += members.get(i) + ",";
        }
      }
    }
    return result;
  }
}
