package com.boogionandon.backend.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

  //  @ElementCollection
  //  @Builder.Default
  //  private List<ProductImage> imageList = new ArrayList<>();
  // 위와 같은 형태로 받기때문에 테이블로 따로 빠지긴 하지만 entity를 새로 파는 것은 아닌...
  // 이 방식의 주요 특징은:
  //
  //별도의 테이블이 생성되지만, ProductImage는 독립적인 엔티티가 아닙니다.
  //주 엔티티(아마도 Product)와 강한 라이프사이클 의존성을 가집니다.
  //주 엔티티를 통해서만 접근 및 관리가 가능합니다.
  //대량의 데이터를 다루기에는 적합하지 않을 수 있습니다.

  // @ElementCollection 방식:
  //ProductImage는 독립적인 엔티티가 아니라 @Embeddable 값 타입입니다.
  //별도의 테이블(product_images)이 생성되지만, ProductImage는 독자적인 ID를 갖지 않습니다.
  //Product와 ProductImage는 강한 라이프사이클 의존성을 가집니다. (Product가 삭제되면 관련 ProductImage도 모두 삭제됩니다)
  //개별 이미지에 대한 직접적인 접근이나 관리가 어렵습니다.

  // 별도의 엔티티로 관리하는 방식:
  //ProductImage는 독립적인 엔티티입니다.
  //각 ProductImage는 고유한 ID를 가집니다.
  //Product와 ProductImage 사이에 양방향 관계가 설정됩니다.
  //개별 이미지에 대한 직접적인 접근과 관리가 가능합니다.
  //더 복잡한 쿼리와 조작이 가능합니다.

  // @ElementCollection 방식으로 일단 진행할 예정 // 빨리 정해야 함




  private String fileName;

  //  ord는 이미지의 순서를 관리하는 필드
  private Integer ord;

  public void setOrd(Integer ord){
    this.ord = ord;
  }
  // 타입 (BEFORE, AFTER)은 따로 지정 안하고 사진을 올릴때 구역에 따라 (BEFORE, AFTER)
  // 앞에 B_, A_ 같이 붙여서 이름을 저장할 생각중

  // 이미지 저장은 썸네일로 바꾸는 디펜던시를 이용해서 원본과 썸네일 되어진 내용을 같이 저장하고
  // 일반적으로 보일때는 썸네일 사진을 보여주고 다운 받던가 사진을 원본을 필요로 하면 그때 원본 보여주기

}
