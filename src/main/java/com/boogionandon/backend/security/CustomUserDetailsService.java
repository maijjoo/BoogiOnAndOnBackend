package com.boogionandon.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

//  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {

    log.info("...............loadUserByUsername................." + username);

    // 나중에 가져올때 다형성으로 (FieldWorker, Collector, Admin) 가져옴 (3중 하나)
//    Member member = memberRepository.getWithRoles(username); // 와 같이
    // 엔티티를 가져와서 instance 체크후 맞는 if 문으로 맞는 DTO를 생성한후
    // 엔티티를 DTO로 변환해주는 작업이 필요 (BaseMemberDTO memberDTO = new FieldWorkerDTO(member); 와 같은 형식으로
    // memberDTO를 최종적으로 반환

    // 아래는 예시
    // Member member = memberRepository.getWithRoles(username)
    //                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    // return convertToDTO(member);

    return null;
  }

  // DTO로 변환하는 로직 (BaseMemberDTO memberDTO = new xxxDTO(member);)
  // 아래처럼 다 풀어 헤치지 않고 엔티티자체를 넘기고 해당 DTO 안에서 생성자를 만들어서 처리해도 됨
  // 아래는 일단 예시(임시)로 만들어놓은 생성자에 넣은 것
  // private BaseMemberDTO convertToDTO(Member member) {
  //        if (member instanceof FieldWorker) {
  //            FieldWorker fieldWorker = (FieldWorker) member;
  //            return new FieldWorkerDTO(
  //                fieldWorker.getId(),
  //                fieldWorker.getEmail(),
  //                fieldWorker.getPassword(),
  //                fieldWorker.getName(),
  //                fieldWorker.getNickname(),
  //                fieldWorker.getPhoneNumber(),
  //                fieldWorker.getRole(),
  //                fieldWorker.getWorkArea(),
  //                fieldWorker.getEquipment(),
  //                fieldWorker.getAvailabilityStatus(),
  //                fieldWorker.getLastActiveDate(),
  //                fieldWorker.getCompletedInvestigations(),
  //                fieldWorker.getCompletedCleanups()
  //            );
  //        } else if (member instanceof Collector) {
  //            Collector collector = (Collector) member;
  //            return new CollectorDTO(
  //                collector.getId(),
  //                collector.getEmail(),
  //                collector.getPassword(),
  //                collector.getName(),
  //                collector.getNickname(),
  //                collector.getPhoneNumber(),
  //                collector.getRole(),
  //                collector.getVehicleType(),
  //                collector.getVehicleLicensePlate(),
  //                collector.getVehicleCapacity(),
  //                collector.getCollectionArea(),
  //                collector.getCompletedCollections(),
  //                collector.getTotalCollectedAmount(),
  //                collector.getStatus(),
  //                collector.getLastCollectionTime()
  //            );
  //        } else if (member instanceof Admin) {
  //            Admin admin = (Admin) member;
  //            return new AdminDTO(
  //                admin.getId(),
  //                admin.getEmail(),
  //                admin.getPassword(),
  //                admin.getName(),
  //                admin.getNickname(),
  //                admin.getPhoneNumber(),
  //                admin.getRole(),
  //                admin.getDepartment(),
  //                admin.getResponsibleArea(),
  //                admin.getEmergencyContact()
  //            );
  //        } else {
  //            throw new IllegalArgumentException("Unknown member type: " + member.getClass().getName());
  //        }
  //    }
}
