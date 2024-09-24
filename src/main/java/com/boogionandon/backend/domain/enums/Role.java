package com.boogionandon.backend.domain.enums;

public enum Role {
  // 나중에 조사자와 청소자는 하나로 합칠 수 있음
  // 그런데 조사자 청소자 권한 주고 수거자는 수거자 권한 주면 별 차이는 없을듯
  FIELD_WORKER, // 조사자, 청소자
  COLLECTOR // 수거자
}
