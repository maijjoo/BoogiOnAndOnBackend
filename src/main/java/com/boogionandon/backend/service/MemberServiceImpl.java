package com.boogionandon.backend.service;


import com.boogionandon.backend.domain.Admin;
import com.boogionandon.backend.domain.Worker;
import com.boogionandon.backend.dto.member.AdminResponseDTO;
import com.boogionandon.backend.dto.member.WorkerResponseDTO;
import com.boogionandon.backend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

  private final MemberRepository memberRepository;
  private final AdminService adminService;

  @Override
  public WorkerResponseDTO getWorkerProfile(Long workerId) {

    Worker worker = (Worker) memberRepository.findByIdWithManager(workerId)
        .orElseThrow(() -> new EntityNotFoundException("Worker not found with id: " + workerId));

    log.info("worker : " + worker);

    Admin admin = (Admin) memberRepository.findByIdWithManager(worker.getManagerId())
        .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + worker.getManagerId()));
    log.info("admin : " + admin);

    List<String> assignmentAreaList = adminService.getAssignmentAreaList(worker.getManagerId());
    log.info("assignmentAreaList : " + assignmentAreaList);

    return WorkerResponseDTO.builder()
        .id(worker.getId())
        .username(worker.getUsername())
        .email(worker.getEmail())
        .name(worker.getName())
        .phone(worker.getPhone())
        .address(worker.getAddress())
        .addressDetail(worker.getAddressDetail())
        .contact(worker.getContact())
        .workGroup(worker.getWorkGroup())
        .workAddress(worker.getWorkAddress())
        .workAddressDetail(worker.getWorkAddressDetail())
        .vehicleCapacity(worker.getVehicleCapacity())
        .startDate(worker.getStartDate())
        .endDate(worker.getEndDate())
        .managerId(worker.getManagerId())
        .managerName(admin.getName())
        .managerDepartment(admin.getDepartment())
        .managerContact(admin.getContact())
        .managerWorkPlace(admin.getWorkPlace())
        .assignmentAreaList(assignmentAreaList)
        .build();
  }

  @Override
  public AdminResponseDTO getAdminProfile(Long adminId) {

    Admin admin = (Admin) memberRepository.findByIdWithManager(adminId)
        .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminId));

    log.info("admin : " + admin);

    List<String> assignmentAreaList = adminService.getAssignmentAreaList(admin.getId());
    log.info("assignmentAreaList : " + assignmentAreaList);

    return AdminResponseDTO.builder()
        .id(admin.getId())
        .username(admin.getUsername())
        .email(admin.getEmail())
        .name(admin.getName())
        .phone(admin.getPhone())
        .address(admin.getAddress())
        .addressDetail(admin.getAddressDetail())
        .workCity(admin.getWorkCity())
        .workPlace(admin.getWorkPlace())
        .department(admin.getDepartment())
        .position(admin.getPosition())
        .contact(admin.getContact())
        .assignmentAreaList(assignmentAreaList)
        .build();
  }
}
