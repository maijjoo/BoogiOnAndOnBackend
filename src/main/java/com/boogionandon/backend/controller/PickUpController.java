package com.boogionandon.backend.controller;


import com.boogionandon.backend.dto.PickUpRequestDTO;
import com.boogionandon.backend.service.CleanService;
import com.boogionandon.backend.service.PickUpService;
import com.boogionandon.backend.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/pickUp")
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

}
