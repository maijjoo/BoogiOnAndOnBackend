package com.boogionandon.backend.dto.admin;

import com.boogionandon.backend.dto.CleanResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrashMapResponseDTO {

  @Builder.Default
  List<CleanResponseDTO> cleanDataList = new ArrayList<>();

}
