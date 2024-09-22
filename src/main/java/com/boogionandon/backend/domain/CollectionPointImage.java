package com.boogionandon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionPointImage {
  @Column(nullable = false)
  private String fileName;

  private String description;

  @Column(nullable = false)
  private int ord;
}