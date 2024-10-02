package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.QBeach;
import com.boogionandon.backend.domain.QClean;
import com.boogionandon.backend.domain.QWorker;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
@RequiredArgsConstructor
public class CleanRepositoryCustomImpl implements CleanRepositoryCustom{

  private final JPAQueryFactory queryFactory;


  // 관리자 페이지에서 지도를 통해 쓰레기 분포도를 보여주는 화면에서 보여줄 내용 뽑아오기
  @Override
  public List<Clean> findByDateCriteria(Integer year, Integer month, LocalDate start, LocalDate end) {

    QClean clean = QClean.clean;
    QWorker worker = QWorker.worker;
    QBeach beach = QBeach.beach;

    // QueryDSL을 사용하여 동적 쿼리 생성
    return queryFactory
        .selectFrom(clean)
        // n + 1 문제를 해결하기 위해 fetchJoin 사용
        // 테스트할때 13번 sql 보내던게 아래 코드를 쓰니 1번으로 끝남
        .leftJoin(clean.cleaner, worker).fetchJoin()
        .leftJoin(clean.beach, beach).fetchJoin()
        .where(
            yearEq(year),
            monthEq(year, month),
            betweenDates(start, end))
        .fetch();
  }

  private BooleanExpression yearEq(Integer year) {
    // year가 null이 아닐 경우에만 조건 생성, 그렇지 않으면 null 반환 (조건 무시)
    return year != null ? QClean.clean.cleanDateTime.year().eq(year) : null;
  }

  private BooleanExpression monthEq(Integer year, Integer month) {
    // year와 month가 모두 null이 아닐 경우에만 조건 생성, 그렇지 않으면 null 반환 (조건 무시)
    return (year != null && month != null) ? QClean.clean.cleanDateTime.month().eq(month) : null;
  }

  private BooleanExpression betweenDates(LocalDate start, LocalDate end) {
    if (start != null && end != null) {
      // 시작일의 00:00:00
      LocalDateTime startOfDay = start.atStartOfDay();
      // 종료일의 23:59:59
      LocalDateTime endOfDay = end.atTime(23,59,59);
      // 두 날짜 사이의 데이터를 조회하는 조건 생성
      return QClean.clean.cleanDateTime.between(startOfDay, endOfDay);
    }
    return null;
  }
}
