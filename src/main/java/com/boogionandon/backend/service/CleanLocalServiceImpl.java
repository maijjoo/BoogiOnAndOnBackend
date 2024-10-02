package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Beach;
import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.TrashType;
import com.boogionandon.backend.dto.CleanRequestDTO;
import com.boogionandon.backend.dto.CleanResponseDTO;
import com.boogionandon.backend.dto.admin.TrashMapResponseDTO;
import com.boogionandon.backend.repository.BeachRepository;
import com.boogionandon.backend.repository.CleanRepository;
import com.boogionandon.backend.repository.MemberRepository;
import com.boogionandon.backend.util.DistanceCalculator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CleanLocalServiceImpl implements CleanService{

  private final CleanRepository cleanRepository;
  private final MemberRepository memberRepository;
  private final BeachRepository beachRepository;

  // clean 보고서 제출
  @Override
  public void insertClean(CleanRequestDTO cleanRequestDTO) {

    Clean clean = createCleanFromDTO(cleanRequestDTO);

    cleanRepository.save(clean);
  }

  // 관리자 페이지에서 쓰레기 분포도 볼때 필요한 메서드
  @Override
  @Transactional(readOnly = true) // 어차피 조회용 이니까
  public TrashMapResponseDTO getTrashDistribution(Integer year, Integer month, LocalDate start, LocalDate end) {
    List<Clean> cleanData = cleanRepository.findByDateCriteria(year, month, start, end);

    TrashMapResponseDTO trashMapResponseDTO = new TrashMapResponseDTO();

    cleanData.forEach(clean -> {
          CleanResponseDTO cleanResponseDTO = CleanResponseDTO.builder()
             .id(clean.getId())
             .cleanerUsername(clean.getCleaner().getUsername())
             .beachName(clean.getBeach().getBeachName())
             .realTrashAmount(clean.getRealTrashAmount())
             .cleanDateTime(clean.getCleanDateTime())
              // 시작, 끝 위경도는 여기에서는 필요없어 null 값으로
              // beachLength도 여기선 필요 없을듯
              // 이미지도 동일
              .mainTrashType(clean.getMainTrashType())
              .fixedLatitude(clean.getBeach().getLatitude())
              .fixedLongitude(clean.getBeach().getLongitude())
             .build();
            trashMapResponseDTO.getCleanDataList().add(cleanResponseDTO);
        });

    log.info("trashMapResponseDTO : " + trashMapResponseDTO);

    return trashMapResponseDTO;
  }

  private Clean createCleanFromDTO(CleanRequestDTO cleanRequestDTO) {

    // 필요한 researcher, beach를 찾고
    Worker cleaner = findCleaner(cleanRequestDTO.getCleanerUsername());
    Beach beach = findBeach(cleanRequestDTO.getBeachName());

    Double startLatitude = cleanRequestDTO.getStartLatitude();
    Double startLongitude = cleanRequestDTO.getStartLongitude();
    Double endLatitude = cleanRequestDTO.getEndLatitude();
    Double endLongitude = cleanRequestDTO.getEndLongitude();

    Double beachLength = DistanceCalculator.calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);

    // DTO에서 받은 값으로 Clean 생성
    Clean clean = Clean.builder()
       .cleaner(cleaner)
       .beach(beach)
        .realTrashAmount(cleanRequestDTO.getRealTrashAmount())
        .cleanDateTime(LocalDateTime.now())
        .startLatitude(startLatitude)
        .startLongitude(startLongitude)
        .endLatitude(endLatitude)
        .endLongitude(endLongitude)
        .beachLength(beachLength)
        .mainTrashType(TrashType.valueOf(cleanRequestDTO.getMainTrashType()))
       .build();

    // 빌더로 하기에는 까다로운 부분을 추가로 설정
    addImages(clean, cleanRequestDTO.getBeforeUploadedFileNames());
    addImages(clean, cleanRequestDTO.getAfterUploadedFileNames());

    return clean;

  }

  private void addImages(Clean clean, List<String> uploadedFileNames) {
    if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
      uploadedFileNames.forEach((fileName) -> {
        clean.addImageString(fileName);
      });
    }
  }

  private Worker findCleaner(String cleanerUsername) {
    return (Worker) memberRepository.findByUsernameWithDetails(cleanerUsername)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이름의 회원을 찾을 수 없습니다. :" + cleanerUsername));
  }

  private Beach findBeach(String beachName) {
    return beachRepository.findById(beachName)
        .orElseThrow(() -> new NoSuchElementException("해당 해안을 찾을 수 없습니다. : " + beachName));
  }
}
