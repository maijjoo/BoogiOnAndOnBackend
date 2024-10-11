package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.PickUp;
import com.boogionandon.backend.domain.QImage;
import com.boogionandon.backend.domain.QMember;
import com.boogionandon.backend.domain.QPickUp;
import com.boogionandon.backend.domain.QWorker;
import com.boogionandon.backend.domain.enums.ReportStatus;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
@RequiredArgsConstructor
public class PickUpRepositoryCustomImpl implements PickUpRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<PickUp> findByStatusCompletedAndSearchForSuper(String beachSearch, Pageable pageable) {

    QPickUp pickUp = QPickUp.pickUp;
    QImage image = QImage.image;

    JPAQuery<PickUp> query = queryFactory
        .selectFrom(pickUp)
        .leftJoin(pickUp.images, image).fetchJoin()
        .where(checkStatusCompletedAndSearch(beachSearch));

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<PickUp> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusCompletedAndSearchForSuper(beachSearch);

    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<PickUp> findByStatusCompletedAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId) {
    QPickUp pickUp = QPickUp.pickUp;
    QImage image = QImage.image;
    QWorker submitter = QWorker.worker;
    QMember member = QMember.member;

    JPAQuery<PickUp> query = queryFactory
        .selectFrom(pickUp)
        .leftJoin(pickUp.images, image).fetchJoin()
        .leftJoin(pickUp.submitter, submitter).fetchJoin()
        .innerJoin(member).on(member.id.eq(submitter.id)) // Worker와 Member를 조인
        .where(
            checkStatusCompletedAndSearch(beachSearch),
            member.managerId.eq(adminId)  // member.managerId를 사용
        );

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<PickUp> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusCompletedAndSearchForRegular(beachSearch, adminId);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  // -------- Completed 시작 -----------

  private Predicate checkStatusCompletedAndSearch(String beachSearch) {
    QPickUp pickUp = QPickUp.pickUp;

    BooleanExpression statusCondition = pickUp.status.eq(ReportStatus.ASSIGNMENT_COMPLETED);

    if (beachSearch != null && !beachSearch.isEmpty()) {
      BooleanExpression searchCondition = pickUp.pickUpPlace.contains(beachSearch);

      return statusCondition.and(searchCondition);
    } else {
      return statusCondition;
    }
  }

  private long countByStatusCompletedAndSearchForSuper(String beachSearch) {
    QPickUp pickUp = QPickUp.pickUp;

    return queryFactory
        .select(pickUp.count())
        .from(pickUp)
        .where(checkStatusCompletedAndSearch(beachSearch))
        .fetchOne();
  }

  private long countByStatusCompletedAndSearchForRegular(String beachSearch, Long adminId) {
    QPickUp pickUp = QPickUp.pickUp;
    QWorker submitter = QWorker.worker;
    QMember member = QMember.member;

    return queryFactory
        .select(pickUp.count())
        .from(pickUp)
        .leftJoin(pickUp.submitter, submitter)
        .innerJoin(member).on(member.id.eq(submitter.id)) // Worker와 Member를 조인
        .where(
            checkStatusCompletedAndSearch(beachSearch),
            member.managerId.eq(adminId)
            )
        .fetchOne();
  }

  // -------- Completed 끝 -----------

  private OrderSpecifier<?> createOrderSpecifier(Sort sort) {
    if (sort.isEmpty()) {
      return null;
    }

    for (Sort.Order order : sort) {
      // 여기 복사 해온것에서 붙여넣기 하고 고쳤는데 정렬 안되면 여기 확인
      PathBuilder<PickUp> pathBuilder = new PathBuilder<>(PickUp.class, "pickUp");

      return new OrderSpecifier(
          order.isAscending() ? Order.ASC : Order.DESC,
          pathBuilder.get(order.getProperty())
      );
    }

    return null;
  }
}
