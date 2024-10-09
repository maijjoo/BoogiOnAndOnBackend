package com.boogionandon.backend.repository.queryDSL;


import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.QAdmin;
import com.boogionandon.backend.domain.QMember;
import com.boogionandon.backend.domain.QWorker;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.domain.enums.MemberType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

  private final JPAQueryFactory queryFactory;


  // 굳이 메서드를 다 나눌 이유가 있을까? queryDSL을 사용한다는게 동적으로 sql문을 만들고 싶다는 건데 메서드를 나누는게....
  @Override
  public Page<Member> findAllByWorkerManagedAdminWithNameSearchForRegular(Long adminId, String tabCondition, String nameSearch, Pageable pageable) {
    QMember member = QMember.member;
    QWorker worker = QWorker.worker;

    JPAQuery<Member> query = queryFactory
        .selectFrom(member)
        .join(worker).on(member.id.eq(worker.id))
        .where(
            getWhereByTabConditionForRegular(adminId, tabCondition, nameSearch)
        );

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifierForRegular(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<Member> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countAllByWorkerManagedAdminWithNameSearchForRegular(adminId, tabCondition, nameSearch);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<Member> findAllByWorkerManagedAdminWithNameSearchForSuper(Long adminId, String tabCondition, String nameSearch, Pageable pageable) {
    QMember member = QMember.member;
    QAdmin admin = QAdmin.admin;
    QWorker worker = QWorker.worker;

// 먼저 슈퍼 관리자의 ID를 찾습니다.
    List<Long> superManagerIds = queryFactory
        .select(admin.id)
        .from(admin)
        .where(admin.memberRoleList.contains(MemberType.SUPER_ADMIN).not()
            .and(admin.managerId.eq(adminId)))
        .fetch();

    log.info("superManagerIds : " + superManagerIds);

    JPAQuery<Member> query = queryFactory
        .selectFrom(member)
        .leftJoin(admin).on(member.id.eq(admin.id))
        .leftJoin(worker).on(member.id.eq(worker.id))
        .where(
            getWhereByTabConditionForSuper(adminId, tabCondition, nameSearch, superManagerIds)
        );

    log.info("query : " + query);


    // 정렬 적용
    List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifierForSuper(pageable.getSort());
    query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));

    log.info("-------");
    List<Member> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);
    log.info("-------");
    long total = countAllByWorkerManagedAdminWithNameSearchForSuper(adminId, tabCondition, nameSearch, superManagerIds);
    log.info("-------");
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }


  // -------------- For Regular 시작 ---------------------
  private static BooleanExpression getWhereByTabConditionForRegular(Long adminId, String tabCondition, String nameSearch) {
    QMember member = QMember.member;
    QWorker worker = QWorker.worker;

    BooleanExpression baseCondition = worker.managerId.eq(adminId)
        // nameSearch 파라미터를 Optional로 감싸줍니다.
        // 이는 nameSearch가 null일 수 있는 상황을 안전하게 처리하기 위함
        .and(Optional.ofNullable(nameSearch)
            // QueryDSL의 조건식으로, 멤버의 이름에 search 문자열이 포함되어 있는지를 검사
            // map은 여기서 Optional의 메서드로 사용되고 있습니다.
            .map(search -> member.name.contains(search))
            // nameSearch가 null인 경우, 이 부분이 실행됩니다.
            .orElse(null));

    switch (tabCondition) {
      case "전체" :
        return baseCondition;
        case "조사/청소" :
        return baseCondition.and(worker.vehicleCapacity.isNull().or(worker.vehicleCapacity.eq(0.0)));
        case "수거자" :
          return baseCondition.and(worker.vehicleCapacity.gt(0));
      default:
        throw new IllegalArgumentException("해당 tabCondition을 찾을 수 없습니다. : " + tabCondition);
    }
  }

  private long countAllByWorkerManagedAdminWithNameSearchForRegular(Long adminId, String tabCondition, String nameSearch) {
    QMember member = QMember.member;
    QWorker worker = QWorker.worker;

    return queryFactory
        .select(member.count())
        .from(member)
        .join(worker).on(member.id.eq(worker.id))
        .where(getWhereByTabConditionForRegular(adminId, tabCondition, nameSearch))
        .fetchOne();
  }

  private OrderSpecifier<?> createOrderSpecifierForRegular(Sort sort) {
    if (sort.isEmpty()) {
      return null;
    }

    for (Sort.Order order : sort) {
      PathBuilder<Worker> pathBuilder = new PathBuilder<>(Worker.class, "worker");

      return new OrderSpecifier(
          order.isAscending()? Order.ASC : Order.DESC,
          pathBuilder.get(order.getProperty())
      );
    }
    return null;
  }

  // -------------- For Regular 끝 ---------------------

  // -------------- For Super 끝 ---------------------
  private static BooleanExpression getWhereByTabConditionForSuper(Long adminId, String tabCondition, String nameSearch, List<Long> superManagerIds) {

    QMember member = QMember.member;
    QWorker worker = QWorker.worker;
    QAdmin admin = QAdmin.admin;

    BooleanExpression nameSearchCondition = Optional.ofNullable(nameSearch)
        .map(search -> member.name.contains(search))
        .orElse(null);

    switch (tabCondition) {
      case "관리자":
        return admin.managerId.eq(adminId).and(nameSearchCondition);
      case "조사/청소":
        return worker.id.isNotNull()
            .and(worker.managerId.in(superManagerIds))
            .and(worker.vehicleCapacity.isNull().or(worker.vehicleCapacity.eq(0.0)))
            .and(nameSearchCondition);
      case "수거자":
        return worker.id.isNotNull()
            .and(worker.managerId.in(superManagerIds))
            .and(worker.vehicleCapacity.isNotNull())
            .and(nameSearchCondition);
      default:
        throw new IllegalArgumentException("해당 tabCondition을 찾을 수 없습니다. : " + tabCondition);
    }
  }

  private long countAllByWorkerManagedAdminWithNameSearchForSuper(Long adminId, String tabCondition, String nameSearch, List<Long> superManagerIds) {
    QMember member = QMember.member;
    QAdmin admin = QAdmin.admin;
    QWorker worker = QWorker.worker;
    return queryFactory
        .select(member.count())
        .from(member)
        .leftJoin(admin).on(member.id.eq(admin.id))
        .leftJoin(worker).on(member.id.eq(worker.id))
        .where(getWhereByTabConditionForSuper(adminId, tabCondition, nameSearch, superManagerIds))
        .fetchOne();
  }

  private List<OrderSpecifier<?>> createOrderSpecifierForSuper(Sort sort) {
    if (sort.isEmpty()) {
      return Collections.emptyList();
    }

    List<OrderSpecifier<?>> orders = new ArrayList<>();
    QMember member = QMember.member;

    for (Sort.Order order : sort) {
      Path<?> path;
      switch (order.getProperty()) {
        case "createdDate":
          path = member.createdDate;
          break;
        case "name":
          path = member.name;
          break;
        // 다른 필드에 대한 case 추가
        default:
          throw new IllegalArgumentException("Unknown sort property: " + order.getProperty());
      }
      orders.add(new OrderSpecifier(
          order.isAscending() ? Order.ASC : Order.DESC,
          path
      ));
    }

    return orders;
  }

  // -------------- For Super 끝 ---------------------


}
