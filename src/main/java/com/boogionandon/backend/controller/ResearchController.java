package com.boogionandon.backend.controller;

import com.boogionandon.backend.dto.BasicPageResponseDTO;
import com.boogionandon.backend.dto.ResearchMainRequestDTO;
import com.boogionandon.backend.service.BeachService;
import com.boogionandon.backend.service.ResearchService;
import com.boogionandon.backend.service.WorkerService;
import com.boogionandon.backend.util.CustomFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/research")
public class ResearchController {

  private final CustomFileUtil fileUtil;
  private final ResearchService researchService;
  private final BeachService beachService;
  private final WorkerService workerService;


  // 현재 DTO로 값을 받으려 하니 files는 MultipartFile로 form-data형식으로 와야 하는데
  // List<ResearchSubList> researchSubList = new List<>(); 가 표현하려면 json으로 들어와야 해서
  // 어쩔수 없이 아래와 같은 형식으로 받음, 나머지는 단건 처리라 DTO로 받아도 될듯

  // { json : {
  //  "researcherUsername": "W_testWorker",
  //  "beachName": "해운대해수욕장",
  //  "beachLength": 18.2,
  //  "expectedTrashAmount": 180,
  //  "weather": "소나기",
  //  "specialNote": "태풍",
  //  "researchSubList": [
  //    {
  //      "beachNameWithIndex": "해운대해수욕장1",
  //      "startLatitude": 35.15768265599188,
  //      "startLongitude": 129.15726481155502,
  //      "endLatitude": 35.15779193363473,
  //      "endLongitude": 129.15770660944662,
  //      "mainTrashType": "폐어구류"
  //    },
  //    {
  //      "beachNameWithIndex": "해운대해수욕장2",
  //      "startLatitude": 35.158029084805435,
  //      "startLongitude": 129.15855778169,
  //      "endLatitude": 35.15829952522936,
  //      "endLongitude": 129.15981583448859,
  //      "mainTrashType": "대형_투기쓰레기류"
  //    }
  //  ]
  //  },
  // files : []
  // }
  // 의 형태로 와야함


  // research 보고서 작성하는 페이지에 내려줄 메서드
  @GetMapping("/{workerId}")
  public BasicPageResponseDTO viewResearchReportPage(@PathVariable("workerId") Long workerId) {

    return BasicPageResponseDTO.builder()
        .beachNameList(beachService.findSortedBeachNameListWithWorkerId(workerId))
        .nameWithNumberList(workerService.findSortedWorkerNameListWithWorkerId(workerId))
        .build();
  }

  @PostMapping("/")
  public Map<String, String> insertResearch(
//      @ModelAttribute ResearchMainRequestDTO mainDTO, 이거 researchSubList안에 넣지를 못함
      @RequestPart("json") String jsonData,
      @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {

    log.info("Received jsonData: {}", jsonData);
    log.info("Received files: {}", files.size());

    ObjectMapper objectMapper = new ObjectMapper();
    log.info("--------------------------------");
    ResearchMainRequestDTO mainRequestDTO = objectMapper.readValue(jsonData, ResearchMainRequestDTO.class);
    log.info("--------------------------------");

    log.info("mainDTO : " + mainRequestDTO);

    List<String> uploadedFileNames = fileUtil.saveFiles(files, "R_");
    mainRequestDTO.setUploadedFileNames(uploadedFileNames);

    log.info("mainDTO.uploadedFileNames" + mainRequestDTO.getUploadedFileNames());

    researchService.insertResearch(mainRequestDTO);

    return Map.of("result", "success");
  }

  @GetMapping("/view/{fileName}")
  public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
    return fileUtil.getFile(fileName);
  }


}
