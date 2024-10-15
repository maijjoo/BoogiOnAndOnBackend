package com.boogionandon.backend.service;

import com.boogionandon.backend.domain.Member;
import com.boogionandon.backend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

  // 현재 서비스의 경우 Local이든 AWS든 상관없이 쓸 수 있다고 생각해 Impl 하지 않음

  private final JavaMailSender emailSender;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  private Map<String, String> verificationCodes = new HashMap<>();

  public void sendVerificationCode(String username, String name) {

    log.info("=== service === username : " + username + ", name : " + name);

    Member findMember = memberRepository.findByUsernameAndName(username, name)
     .orElseThrow(() -> new EntityNotFoundException("해당 username과 name을 찾을 수 없습니다. : " + username + ", " + name));

    log.info("findMember : " + findMember);

    String code = generateVerificationCode();
    verificationCodes.put(findMember.getEmail(), code);
    log.info("verificationCodes.value : " + verificationCodes.get(findMember.getEmail()));
    sendEmail(findMember.getEmail(), "비밀번호 재설정 인증 코드", "인증 코드: " + code);
  }

  public boolean verifyCode(String username, String name, String code) {

    Member findMember = memberRepository.findByUsernameAndName(username, name)
        .orElseThrow(() -> new EntityNotFoundException("해당 username과 name을 찾을 수 없습니다. : " + username + ", " + name));

    log.info("findMember : " + findMember);
    log.info("verificationCodes.value : " + verificationCodes.get(findMember.getEmail()));
    log.info("code : " + code);

    String storedCode = verificationCodes.get(findMember.getEmail());
    return storedCode != null && storedCode.equals(code);
  }

  public void resetPassword(String username, String name, String newPassword) {
    // 실제 구현에서는 데이터베이스에서 사용자를 찾아 비밀번호를 업데이트해야 합니다.

    Member findMember = memberRepository.findByUsernameAndName(username, name)
        .orElseThrow(() -> new EntityNotFoundException("해당 username과 name을 찾을 수 없습니다. : " + username + ", " + name));


    findMember.changePassword(passwordEncoder.encode(newPassword));
    log.info("패스워드가 변경되었습니다.");
    verificationCodes.remove(findMember.getEmail());
  }

  private String generateVerificationCode() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(1000000));
  }

  private void sendEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

}
