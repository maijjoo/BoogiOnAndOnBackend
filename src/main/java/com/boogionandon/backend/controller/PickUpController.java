package com.boogionandon.backend.controller;


import com.boogionandon.backend.dto.PickUpListForCollectorResponseDTO;
import com.boogionandon.backend.dto.PickUpRequestDTO;
import com.boogionandon.backend.service.PickUpService;
import com.boogionandon.backend.util.CustomFileUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/pick-up")
public class PickUpController {
    private final CustomFileUtil fileUtil;
    private final PickUpService pickUpService;

    @PostMapping("/")
    public Map<String, String> insertPickUp(PickUpRequestDTO pickUpRequestDTO) {
        List<MultipartFile> files = pickUpRequestDTO.getFiles();
        List<String> uplodedFileNames = fileUtil.saveFiles(files, "P_");
        pickUpRequestDTO.setUploadedFileNames(uplodedFileNames);

        pickUpService.insertPickUp(pickUpRequestDTO);

        return Map.of("result", "success");
    }

    @GetMapping("/{adminId}")
    public List<PickUpListForCollectorResponseDTO> getPickUpListForCollector(@PathVariable Long adminId) {

        return pickUpService.findPickUpWithAdmin(adminId);
    }

    // TODO : 여기 부터
    @PatchMapping("/added-route/{pickUpId}")
    public Map<String, String> updatePickUpStatusToAddedToRoute(@PathVariable Long pickUpId) {
        pickUpService.updatePickUpStatusToAddedToRoute(pickUpId);
        return Map.of("result", "success");
    }

    @PatchMapping("/completed/{pickUpId}")
    public Map<String, String> updatePickUpStatusToCompleted(@PathVariable Long pickUpId) {
        pickUpService.updatePickUpStatusToCompleted(pickUpId);
        return Map.of("result", "success");
    }

    @PatchMapping("/cancel/{pickUpId}")
    public Map<String, String> updatePickUpStatusToCanceled(@PathVariable Long pickUpId) {
        pickUpService.updatePickUpStatusFromAddedToNeeded(pickUpId);
        return Map.of("result", "success");
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }


}
