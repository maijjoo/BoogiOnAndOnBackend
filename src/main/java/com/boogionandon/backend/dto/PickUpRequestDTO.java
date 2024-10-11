package com.boogionandon.backend.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PickUpRequestDTO {
//    private Long id;            // ID
    private String submitterUsername;   // 제출자 이름
    private String pickUpPlace; // 집하지 명
    private Double latitude;    // 위도
    private Double longitude;   // 경도
    private String mainTrashType;    //주요 쓰레기 타입
    private Integer realTrashAmount;   // 실제 수거량

    // 등록/수정용의 진짜 파일들
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    // 조회용으로 쓸 파일의 이름만 있는 List
    @Builder.Default
    private List<String> uploadedFileNames = new ArrayList<>();

}
