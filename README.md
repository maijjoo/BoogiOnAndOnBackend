# boogiOnAndOn : 비치 세이버

## 프로젝트 개요
비치 세이버는 [여기에 프로젝트 간단한 설명을 추가하세요]

## 데이터베이스 정보
- Host: localhost
- Port: 33061 (호스트) -> 3306 (컨테이너)
- Database: boogiOnAndOn
- Username: boogiOnAndOn
- Password: [비밀번호는 보안을 위해 직접 기록하지 않음]

## 개발 환경 설정

### 사전 요구사항
- Docker 설치
- MySQL 클라이언트 (선택사항)

### Docker를 이용한 데이터베이스 설정

1. Docker 이미지 pull:
   ```
   docker pull dan556/boogi-mysql:v1
   ```

2. Docker 컨테이너 실행:
   ```
   docker run -d \
     --name mysql-boogiOnAndOn \
     -p 33061:3306 \
     dan556/boogi-mysql:v1
   ```

3. 컨테이너 상태 확인:
   ```
   docker ps
   ```

4. MySQL 접속 (선택사항):
   ```
   mysql -h 127.0.0.1 -P 33061 -u boogiOnAndOn -p
   ```
   비밀번호 입력 요청 시 설정한 비밀번호를 입력하세요.

### 애플리케이션 설정

1. 프로젝트 클론:
   ```
   git clone [your-repository-url]
   cd boogiOnAndOn
   ```

2. 애플리케이션 실행:
   ```
   ./gradlew bootRun
   ```
   또는
   ```
   ./mvnw spring-boot:run
   ```
   (사용하는 빌드 도구에 따라 선택)

## 주의사항
- JPA를 사용하여 테이블을 자동 생성합니다. 수동으로 테이블을 생성할 필요가 없습니다.
- 이 Docker 이미지에는 이미 설정된 데이터베이스 구조와 사용자 정보가 포함되어 있습니다.
- 실제 운영 환경에서는 보안을 위해 데이터베이스 비밀번호를 변경하고, 환경 변수 등을 통해 안전하게 관리해야 합니다.

## 문제 해결
- 포트 충돌 발생 시:
  33061 포트가 이미 사용 중이라면, Docker 실행 명령어의 `-p` 옵션을 수정하여 다른 포트를 사용하세요.
- 컨테이너 접속 문제 발생 시:
  `docker logs mysql-boogiOnAndOn` 명령어로 로그를 확인하세요.

## 개발자를 위한 정보

### 데이터베이스 이미지 업데이트
데이터베이스 스키마나 초기 데이터를 변경해야 할 경우:
1. 컨테이너에서 필요한 변경 작업을 수행합니다.
2. 새 버전의 이미지를 생성합니다:
   ```
   docker commit mysql-boogiOnAndOn dan556/boogi-mysql:v2
   ```
3. 새 이미지를 Docker Hub에 푸시합니다:
   ```
   docker push dan556/boogi-mysql:v2
   ```
4. README를 업데이트하고 팀원들에게 새 버전 사용을 안내합니다.

## 기여 방법
[프로젝트에 기여하는 방법에 대한 간단한 가이드라인을 추가하세요]

## 라이선스
[프로젝트의 라이선스 정보를 추가하세요]로 테이블을 생성할 필요가 없습니다.