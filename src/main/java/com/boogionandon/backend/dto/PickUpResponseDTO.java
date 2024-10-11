package com.boogionandon.backend.dto;


import com.boogionandon.backend.domain.enums.TrashType;
import lombok.Data;

@Data
public class PickUpResponseDTO {
    private Long id;            // ID
    private String pickUpPlace; // 집하지 명
    private Double latitude;    // 위도
    private Double longitude;   // 경도
    private TrashType mainTrashType;    //주요 쓰레기 타입
    private Double actualCollectedVolume;   // 실제 수거량
}
