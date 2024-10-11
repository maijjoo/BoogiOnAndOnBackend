package com.boogionandon.backend.repository.queryDSL;

import com.boogionandon.backend.domain.Clean;
import com.boogionandon.backend.domain.QBeach;
import com.boogionandon.backend.domain.QClean;
import com.boogionandon.backend.domain.QImage;
import com.boogionandon.backend.domain.QMember;
import com.boogionandon.backend.domain.QWorker;
import com.boogionandon.backend.domain.enums.ReportStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class CleanRepositoryCustomImpl implements CleanRepositoryCustom {

  private final JPAQueryFactory queryFactory;


  // 관리자 페이지에서 지도를 통해 쓰레기 분포도를 보여주는 화면에서 보여줄 내용 뽑아오기
  @Override
  public List<Clean> findByDateCriteria(Integer year, Integer month, LocalDate start,
      LocalDate end) {

    QClean clean = QClean.clean;
    QWorker worker = QWorker.worker;
    QBeach beach = QBeach.beach;

    // QueryDSL을 사용하여 동적 쿼리 생성
    return queryFactory
        .selectFrom(clean)
        // n + 1 문제를 해결하기 위해 fetchJoin 사용
        // 테스트할때 n번 sql 보내던게 아래 코드를 쓰니 1번으로 끝남
        .leftJoin(clean.cleaner, worker).fetchJoin()
        .leftJoin(clean.beach, beach).fetchJoin()
        .where(
            yearEq(year),
            monthEq(year, month),
            betweenDates(start, end))
        .fetch();
  }
  // ----------- findByDateCriteria 관련 시작 ----------------------

  private BooleanExpression yearEq(Integer year) {
    // year가 null이 아닐 경우에만 조건 생성, 그렇지 않으면 null 반환 (조건 무시)
    return year != null ? QClean.clean.cleanDateTime.year().eq(year) : null;
  }

  private BooleanExpression monthEq(Integer year, Integer month) {
    // year와 month가 모두 null이 아닐 경우에만 조건 생성, 그렇지 않으면 null 반환 (조건 무시)
    // 아래가 yearEq에서 이미 year가 true가 되어 있다고 판단하기 때문에 month만 판별
    return (year != null && month != null) ? QClean.clean.cleanDateTime.month().eq(month) : null;
  }

  private BooleanExpression betweenDates(LocalDate start, LocalDate end) {
    if (start != null && end != null) {
      // 시작일의 00:00:00
      LocalDateTime startOfDay = start.atStartOfDay();
      // 종료일의 23:59:59
      LocalDateTime endOfDay = end.atTime(23, 59, 59);
      // 두 날짜 사이의 데이터를 조회하는 조건 생성
      return QClean.clean.cleanDateTime.between(startOfDay, endOfDay);
    }
    return null;
  }
  // ----------- findByDateCriteria 관련 끝 ----------------------

  // 관리자 페이지에서 기초 통계를 보영주는 화면에서 보여줄 내용 뽑아오기
  @Override
  public List<Clean> getBasicStatistics(String tapCondition, Integer year, Integer month, String beachName) {
    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;


    return queryFactory
        .selectFrom(clean)
        .leftJoin(clean.beach, beach).fetchJoin()
        .leftJoin(clean.cleaner, QWorker.worker).fetchJoin()
        .where(getBeachStatusInPeriod(tapCondition, year, month, beachName))
        .orderBy(clean.cleanDateTime.asc())
        .fetch();
  }

  // TODO : 자신이 담당하는 것들만 가져오기 but 수퍼 관리자는 자신의 아래 만들어진 모든 정보 확인(나중에 시간되면 하기)
  @Override
  public Page<Clean> findByStatusNeededAndSearchForSuper(String beachSearch, Pageable pageable) {
    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;
    QImage image = QImage.image;

    JPAQuery<Clean> query = queryFactory
        .selectFrom(clean)
        .leftJoin(clean.beach, beach).fetchJoin()
        .leftJoin(clean.images, image).fetchJoin()
        .where(checkStatusNeededAndSearch(beachSearch));

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<Clean> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusNeededAndSearchForSuper(beachSearch);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }
  @Override
  public Page<Clean> findByStatusNeededAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId) {
    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;
    QImage image = QImage.image;
    QWorker cleaner = QWorker.worker;
    QMember member = QMember.member;

    JPAQuery<Clean> query = queryFactory
        .selectFrom(clean)
        .leftJoin(clean.beach, beach).fetchJoin()
        .leftJoin(clean.images, image).fetchJoin()
        .leftJoin(clean.cleaner, cleaner).fetchJoin()
        .innerJoin(member).on(member.id.eq(cleaner.id)) // Worker와 Member를 조인
        .where(
            checkStatusNeededAndSearch(beachSearch),
            member.managerId.eq(adminId)); // member.managerId를 사용

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<Clean> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusNeededAndSearchForRegular(beachSearch, adminId);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<Clean> findByStatusCompletedAndSearchForSuper(String beachSearch, Pageable pageable) {

    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;
    QImage image = QImage.image;

    JPAQuery<Clean> query = queryFactory
        .selectFrom(clean)
        .leftJoin(clean.beach, beach).fetchJoin()
        .leftJoin(clean.images, image).fetchJoin()
        .where(checkStatusCompletedAndSearch(beachSearch));

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<Clean> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusCompletedAndSearchForSuper(beachSearch);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }
  @Override
  public Page<Clean> findByStatusCompletedAndSearchForRegular(String beachSearch, Pageable pageable, Long adminId) {

    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;
    QImage image = QImage.image;
    QWorker cleaner = QWorker.worker;
    QMember member = QMember.member;

    JPAQuery<Clean> query = queryFactory
        .selectFrom(clean)
        .leftJoin(clean.beach, beach).fetchJoin()
        .leftJoin(clean.images, image).fetchJoin()
        .leftJoin(clean.cleaner, cleaner).fetchJoin()
        .innerJoin(member).on(member.id.eq(cleaner.id)) // Worker와 Member를 조인
        .where(
            checkStatusCompletedAndSearch(beachSearch),
            member.managerId.eq(adminId) // member.managerId를 사용
        );

    // 정렬 적용
    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(pageable.getSort());
    if (orderSpecifier != null) {
      query.orderBy(orderSpecifier);
    }

    List<Clean> content = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    log.info("content : " + content);

    long total = countByStatusCompletedAndSearchForRegular(beachSearch, adminId);
    log.info("total : " + total);

    return new PageImpl<>(content, pageable, total);
  }

  // ------------- Needed 시작-------------------
  private long countByStatusNeededAndSearchForSuper(String search) {
    QClean clean = QClean.clean;

    return queryFactory
        .select(clean.count())
        .from(clean)
        .where(checkStatusNeededAndSearch(search))
        .fetchOne();
  }
  private long countByStatusNeededAndSearchForRegular(String search, Long adminId) {
    QClean clean = QClean.clean;
    QWorker cleaner = QWorker.worker;
    QMember member = QMember.member; // Member 엔티티에 대한 Q 클래스를 추가 // 상속 받기 때문인것 같음

    return queryFactory
        .select(clean.count())
        .from(clean)
        .leftJoin(clean.cleaner, cleaner)
        .innerJoin(member).on(member.id.eq(cleaner.id)) // Worker와 Member를 조인
        .where(
            checkStatusNeededAndSearch(search),
            member.managerId.eq(adminId)) // member.managerId를 사용)
        .fetchOne();
  }

  private Predicate checkStatusNeededAndSearch(String beachSearch) {
    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;

    BooleanExpression statusCondition = clean.status.eq(ReportStatus.ASSIGNMENT_NEEDED);

    if (beachSearch != null && !beachSearch.isEmpty()) {
      BooleanExpression searchCondition = beach.beachName.contains(beachSearch);

      return statusCondition.and(searchCondition);
    } else {
      return statusCondition;
    }
  }

  // ------------- Needed 끝-------------------
  // ------------- Completed 시작-------------------

  private Predicate checkStatusCompletedAndSearch(String beachSearch) {
    QClean clean = QClean.clean;
    QBeach beach = QBeach.beach;

    BooleanExpression statusCondition = clean.status.eq(ReportStatus.ASSIGNMENT_COMPLETED);

    if (beachSearch != null && !beachSearch.isEmpty()) {
      BooleanExpression searchCondition = beach.beachName.contains(beachSearch);

      return statusCondition.and(searchCondition);
    } else {
      return statusCondition;
    }
  }

  private long countByStatusCompletedAndSearchForSuper(String search) {
    QClean clean = QClean.clean;

    return queryFactory
        .select(clean.count())
        .from(clean)
        .where(checkStatusCompletedAndSearch(search))
        .fetchOne();
  }

  private long countByStatusCompletedAndSearchForRegular(String search, Long adminId) {
    QClean clean = QClean.clean;
    QWorker cleaner = QWorker.worker;
    QMember member = QMember.member; // Member 엔티티에 대한 Q 클래스를 추가 // 상속 받기 때문인것 같음

    return queryFactory
        .select(clean.count())
        .from(clean)
        .leftJoin(clean.cleaner, cleaner)
        .innerJoin(member).on(member.id.eq(cleaner.id)) // Worker와 Member를 조인
        .where(
            checkStatusCompletedAndSearch(search),
            member.managerId.eq(adminId)) // member.managerId를 사용)
        .fetchOne();
  }

  // ------------- Completed 끝-------------------

  private OrderSpecifier<?> createOrderSpecifier(Sort sort) {
    if (sort.isEmpty()) {
      return null;
    }

    for (Sort.Order order : sort) {
      // 여기 복사 해온것에서 붙여넣기 하고 고쳤는데 정렬 안되면 여기 확인
      PathBuilder<Clean> pathBuilder = new PathBuilder<>(Clean.class, "clean");

      return new OrderSpecifier(
          order.isAscending() ? Order.ASC : Order.DESC,
          pathBuilder.get(order.getProperty())
      );
    }

    return null;
  }

  private Predicate getBeachStatusInPeriod(String tapCondition, Integer year, Integer month, String beachName) {
    QClean clean = QClean.clean;


    BooleanExpression timeCondition = null;
    // 일단 tapCondition이 화면 버튼에 있는 글자 그대로 가져 온다고 보고 한글로 처리함 (연도별, 월별, 일별)
    switch (tapCondition) {
      case "연도별":
        // 요구사항이 작년 포함 5년이기 때문에 (해당년도 X) (이전년도 -4 ~ 이전년도)
        Integer lastYear = LocalDate.now().getYear() - 1;
        timeCondition = clean.cleanDateTime.year().between(lastYear - 4, lastYear);
        break;
      case "월별":
        // beachName이 null이거나 빈 문자열이 아닐 때만 해변 이름 조건 추가
        timeCondition = clean.cleanDateTime.year().eq(year);
        break;
      case "일별":
        // 기초통계 기간별(일별) 메인(DEFAULT 작년 12월 1 ~ 31일, 전지역)
        timeCondition = clean.cleanDateTime.year().eq(year).and(clean.cleanDateTime.month().eq(month));
        break;
      default:
        throw new IllegalArgumentException("Invalid tapCondition: " + tapCondition);
    }
    log.info("timeCondition : " +timeCondition.toString());

    if (beachName != null && !beachName.trim().isEmpty()) {
      BooleanExpression beachCondition = clean.beach.beachName.contains(beachName);
      log.info("Adding beach condition for: {}", beachName);
      log.info("beachCondition : " +beachCondition.toString());

      return timeCondition.and(beachCondition);
    } else {
      log.info("No beach condition applied : " + beachName);
      return timeCondition;
    }
  }
}