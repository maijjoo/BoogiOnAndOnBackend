package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.QBeach;
import com.boogionandon.backend.domain.QResearchMain;
import com.boogionandon.backend.domain.QResearchSub;
import com.boogionandon.backend.domain.ResearchMain;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;


@Repository
@Log4j2
@RequiredArgsConstructor
public class ResearchMainRepositoryCustomImpl implements ResearchMainRepositoryCustom{

  private final JPAQueryFactory queryFactory;


  // New 작업에 관한 메서드
  // 조사 완료탭에 들어감 -> 조사는 완료했지만 배정은 되지 않은 상태
  @Override
  public List<ResearchMain> findByStatusNeededAndSearch(String search) {

    QResearchMain researchMain = QResearchMain.researchMain;
    QResearchSub researchSub = QResearchSub.researchSub;
    QBeach beach = QBeach.beach;

    return queryFactory
        .selectFrom(researchMain)
        // 이게 맞나?
        .leftJoin(researchMain.researchSubList, researchSub).fetchJoin()
        .leftJoin(researchMain.beach, beach).fetchJoin()
        .where(
            checkStatusAndSearch(search))
        .fetch();
  }

  private Predicate checkStatusAndSearch(String search) {
    QResearchMain researchMain = QResearchMain.researchMain;
    QBeach beach = QBeach.beach;

    BooleanExpression statusCondition = researchMain.status.eq(ReportStatus.ASSIGNMENT_NEEDED);

    if (search != null && !search.isEmpty()) {
      BooleanExpression searchCondition = beach.beachName.contains(search);

      return statusCondition.and(searchCondition);
    } else {
      return statusCondition;
    }
  }

  // 작업 조회에 관한 메서드

}
