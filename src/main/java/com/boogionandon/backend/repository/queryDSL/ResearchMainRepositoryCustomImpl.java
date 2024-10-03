package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.QBeach;
import com.boogionandon.backend.domain.QImage;
import com.boogionandon.backend.domain.QResearchMain;
import com.boogionandon.backend.domain.QResearchSub;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.boogionandon.backend.dto.PageRequestDTO;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;


@Repository
@Log4j2
@RequiredArgsConstructor
public class ResearchMainRepositoryCustomImpl implements ResearchMainRepositoryCustom{

  private final JPAQueryFactory queryFactory;


  // New 작업에 관한 메서드
  // 조사 완료탭에 들어감 -> 조사는 완료했지만 배정은 되지 않은 상태
  @Override
  public Page<ResearchMain> findByStatusNeededAndSearch(String beachSearch, Pageable pageable) {

    QResearchMain researchMain = QResearchMain.researchMain;
    QResearchSub researchSub = QResearchSub.researchSub;
    QBeach beach = QBeach.beach;


    JPAQuery<ResearchMain> query = queryFactory
        .selectFrom(researchMain)
        // 이게 맞나?
        .leftJoin(researchMain.researchSubList, researchSub).fetchJoin()
        .leftJoin(researchMain.beach, beach).fetchJoin()
        .where(checkStatusAndSearch(beachSearch));

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<ResearchMain> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusNeededAndSearch(beachSearch);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  private Predicate checkStatusAndSearch(String beachSearch) {
    QResearchMain researchMain = QResearchMain.researchMain;
    QBeach beach = QBeach.beach;

    BooleanExpression statusCondition = researchMain.status.eq(ReportStatus.ASSIGNMENT_NEEDED);

    if (beachSearch != null && !beachSearch.isEmpty()) {
      BooleanExpression searchCondition = beach.beachName.contains(beachSearch);

      return statusCondition.and(searchCondition);
    } else {
      return statusCondition;
    }
  }

  private long countByStatusNeededAndSearch(String search) {
    QResearchMain researchMain = QResearchMain.researchMain;

    return queryFactory
        .select(researchMain.count())
        .from(researchMain)
        .where(checkStatusAndSearch(search))
        .fetchOne();
  }

  private OrderSpecifier<?> createOrderSpecifier(Sort sort) {
    if (sort.isEmpty()) {
      return null;
    }

    for (Sort.Order order : sort) {
      PathBuilder<ResearchMain> pathBuilder = new PathBuilder<>(ResearchMain.class, "researchMain");

      return new OrderSpecifier(
          order.isAscending() ? Order.ASC : Order.DESC,
          pathBuilder.get(order.getProperty())
      );
    }

    return null;
  }

  // 작업 조회에 관한 메서드

}
