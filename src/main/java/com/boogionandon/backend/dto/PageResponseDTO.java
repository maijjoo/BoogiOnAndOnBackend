package com.boogionandon.backend.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.Data;


// 아래 있는 <E> 는 제네릭 입니다. 따로 공부하세요
@Data
public class PageResponseDTO<E> {

  private List<E> dtoList;

  private List<Integer> pageNumberList;

  private PageRequestDTO pageRequestDTO;

  private boolean prev, next;

  private int totalCount, prevPage, nextPage, totalPage, current;

  // Builder Pattern
  // 이 메서드를 사용하면 파라미터 쪽의 3가지만 넣어주면 나머지는 안에서 다 채워줌
  @Builder(builderMethodName = "withAll")
  public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount) {

    this.dtoList = dtoList;
    this.pageRequestDTO = pageRequestDTO;

    this.totalCount = (int) totalCount;

    // 끝 페이지 end
    // pageRequestDTO.getPage(): 현재 페이지 번호를 가져옵니다.
    //pageRequestDTO.getPage() / 10.0: 현재 페이지 번호를 10으로 나눕니다. 여기서 10.0을 사용한 이유는 소수점 결과를 얻기 위해서입니다.
    //Math.ceil(...): 결과를 올림합니다. 이는 현재 페이지가 속한 그룹 번호를 계산하는 효과가 있습니다.
    //(...) * 10: 올림한 결과에 10을 곱합니다. 이는 해당 그룹의 마지막 페이지 번호를 계산합니다.
    //(int): 최종 결과를 정수로 캐스팅합니다.
    //
    //ex)
    //페이지 1~10: end = 10
    //페이지 11~20: end = 20
    //페이지 21~30: end = 30
    int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;

    int start = end - 9;

    // 진짜 마지막 페이지 (최종 페이지 번호)
    //totalCount: 전체 데이터(항목)의 개수입니다.
    //pageRequestDTO.getSize(): 한 페이지에 표시되는 항목의 개수입니다.
    //totalCount / (double) pageRequestDTO.getSize(): 전체 데이터 수를 페이지 크기로 나눕니다. double로 캐스팅하여 소수점 결과를 얻습니다.
    //Math.ceil(...): 결과를 올림합니다. 이는 부분적으로 채워진 페이지도 하나의 페이지로 계산하기 위함입니다.
    //(int): 최종 결과를 정수로 캐스팅합니다.
    //
    //ex)
    //전체 데이터 수(totalCount)가 95개이고, 페이지 크기(size)가 10이라고 가정해봅시다.
    //95 / 10.0 = 9.5
    //Math.ceil(9.5) = 10
    //따라서 last = 10
    //전체 데이터 수가 100개이고, 페이지 크기가 10인 경우:
    //100 / 10.0 = 10.0
    //Math.ceil(10.0) = 10
    //따라서 last = 10
    //전체 데이터 수가 101개이고, 페이지 크기가 10인 경우:
    //101 / 10.0 = 10.1
    //Math.ceil(10.1) = 11
    //따라서 last = 11
    int last = (int) (Math.ceil(totalCount / (double) pageRequestDTO.getSize()));

    // 이 로직의 목적은 계산된 페이지 그룹의 마지막 번호(end)가
    // 실제 데이터에 기반한 마지막 페이지 번호(last)를 초과하지 않도록 보장하는 것
    end = end > last? last : end;

    // prev, next가 나오게 할지 말지 결정하는 boolean값
    this.prev = start > 1;
    this.next = totalCount > end * pageRequestDTO.getSize();


    // IntStream.rangeClosed(start, end):
    //start부터 end까지의 연속된 정수 스트림을 생성합니다.
    //rangeClosed는 끝 값(end)을 포함합니다.
    //
    //.boxed():
    //기본 타입인 int를 래퍼 클래스인 Integer로 박싱합니다.
    //이는 collect 메서드를 사용하기 위해 필요합니다.
    // 기본타입(primitive type은 List, Set같은 Collection Type에 들어가지 않음,
    // 이를 int -> Integer, double -> Double ... 와 같은 형식으로 변경 시켜줌
    //
    //.collect(Collectors.toList()):
    //스트림의 요소들을 List<Integer>로 수집합니다.
    //
    //예를 들어, start가 1이고 end가 5라면, 이 코드는 [1, 2, 3, 4, 5]라는 리스트를 생성하여 pageNumberList에 저장합니다.
    this.pageNumberList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

    this.prevPage = prev ? start -1 : 0;
    this.nextPage = next ? end + 1 : 0;

    // 현재 페이지 그룹에 있는 페이지 번호의 총 개수를 계산하여 totalPage 변수에 저장
    // 현재 페이지 그룹의 마지막 페이지인지 확인할 때 사용할 수 있습니다.
    //예: if (currentPageIndex == totalPage - 1) { /* 마지막 페이지 처리 */ }
    // 사용자에게 "5 페이지 중 3 페이지" 같은 형식으로 현재 페이지 위치를 표시할 때 사용할 수 있습니다.
    this.totalPage = this.pageNumberList.size();

    // QueryString 으로 들어오는 현재페이지
    this.current = pageRequestDTO.getPage();

  }
}
