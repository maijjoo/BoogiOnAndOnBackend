package com.boogionandon.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicPageResponseDTO {

  List<String> beachNameList;

  List<String> nameWithNumberList;

}
