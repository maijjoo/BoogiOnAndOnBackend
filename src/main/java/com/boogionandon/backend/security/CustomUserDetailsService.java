package com.boogionandon.backend.security;

import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.dto.AdminDTO;
import com.boogionandon.backend.dto.WorkerDTO;
import com.boogionandon.backend.repository.MemberRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {

    log.info("...............loadUserByUsername................." + username);

    // 나중에 가져올때 다형성으로 (Worker, Admin) 가져옴 (2중 하나)
    Member member = memberRepository.findByUsernameWithDetails(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)); // 와 같이
    // 엔티티를 가져와서 instance 체크후 맞는 if 문으로 맞는 DTO를 생성한후
    // 엔티티를 DTO로 변환해주는 작업이 필요 (BaseMemberDTO memberDTO = new FieldWorkerDTO(member); 와 같은 형식으로
    // memberDTO를 최종적으로 반환
    // 아래가 되나??? 싱글 테이블 전략으로 이게 Admin인지 Worker인지 구분이 되나??
    if (member instanceof Admin) {
      return convertToAdminDTO((Admin) member);
    } else if (member instanceof Worker) {
      return convertToWorkerDTO((Worker) member);
    } else {
      throw new RuntimeException("Unknown Member Type");
    }

  }

  private AdminDTO convertToAdminDTO(Admin admin) {
    return new AdminDTO(
        admin.getId(),
        admin.getUsername(),
        admin.getPassword(),
        admin.getEmail(),
        admin.getName(),
        admin.getPhone(),
        admin.getAddress(),
        admin.getAddressDetail(),
        admin.getMemberRoleList()
            .stream()
            .map(role -> role.name())
            .collect(Collectors.toList()),
        admin.getWorkCity(),
        admin.getWorkPlace(),
        admin.getDepartment(),
        admin.getPosition(),
        admin.getContact(),
        admin.getManagerId(),
        admin.isDelFlag()
    );
  }


  private WorkerDTO convertToWorkerDTO(Worker worker) {
    return new WorkerDTO(
        worker.getId(),
        worker.getUsername(),
        worker.getPassword(),
        worker.getEmail(),
        worker.getName(),
        worker.getPhone(),
        worker.getBirth(),
        worker.getAddress(),
        worker.getAddressDetail(),
        worker.getMemberRoleList()
              .stream()
              .map(role -> role.name())
              .collect(Collectors.toList()),
        worker.getVehicleCapacity(),
        worker.getManagerId(),
        worker.isDelFlag()
     );
  }
}
