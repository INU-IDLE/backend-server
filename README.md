# RushCutter Backend Server

## Overview
RushCutter 백엔드 서버는 Spring Boot 기반으로 개발되었으며, PostgreSQL을 사용합니다.  
Swagger UI를 지원하며, Docker Compose를 사용하여 쉽게 실행할 수 있습니다.

---

## 1. 프로젝트 디렉터리 구조

```
backend-server
├── Dockerfile                   # 백엔드 애플리케이션 Docker 빌드 설정
├── docker-compose.yaml          # Docker Compose 설정 파일
├── build.gradle.kts             # Gradle 빌드 스크립트
├── settings.gradle.kts          # Gradle 프로젝트 설정
├── gradlew, gradlew.bat         # Gradle Wrapper 실행 파일 (Windows/macOS 지원)
├── gradle/                      # Gradle 관련 설정 파일 (Wrapper 포함)
│   └── wrapper/
│       ├── gradle-wrapper.jar         # Gradle Wrapper 실행 파일
│       └── gradle-wrapper.properties  # Gradle Wrapper 설정
├── src/
│   ├── main/                       # 애플리케이션 소스 코드
│   │   ├── java/com/idle/rushcutter/
│   │   │   ├── RushcutterApplication.java  # 메인 애플리케이션 실행 파일
│   │   │   ├── config/             # 설정 관련 파일
│   │   │   ├── controller/         # 컨트롤러 (API 엔드포인트)
│   │   │   ├── service/            # 비즈니스 로직
│   │   │   ├── repository/         # 데이터 접근 레이어
│   │   │   ├── entity/             # JPA 엔티티 클래스
│   │   │   ├── dto/                # 데이터 전송 객체
│   │   │   ├── exception/          # 예외 처리
│   │   │   └── util/               # 유틸리티 클래스
│   │   ├── resources/
│   │   │   ├── application.properties  # Spring Boot 설정 파일
│   │   │   ├── db/migration/       # DB 마이그레이션 스크립트 (Flyway)
│   │   │   ├── static/             # 정적 파일
│   │   │   └── templates/          # 템플릿 파일
│   └── test/                       # 테스트 코드
├── init.sql                        # PostgreSQL 초기 설정 SQL 스크립트
├── .env                            # 환경 변수 파일 (DB 및 애플리케이션 설정)
├── .gitignore                      # Git에서 제외할 파일 목록
└── README.md                       # 프로젝트 실행 방법 및 설명
```

---

## 2. 실행 방법

### 2.1 환경 변수 설정
이 프로젝트는 `.env` 파일을 사용하여 민감한 정보를 관리합니다.  
`.env` 파일을 프로젝트 루트에 생성하고 다음과 같이 설정합니다:

```dotenv
COMPOSE_PROJECT_NAME=rush_cutter

# PostgreSQL 설정
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_URL=
POSTGRES_HOST=
POSTGRES_PORT=

# Spring Boot 환경 변수
SPRING_JPA_HIBERNATE_DDL_AUTO=
SPRING_JPA_SHOW_SQL=
SPRING_PROFILES_ACTIVE=
springdoc.api-docs.enabled=
springdoc.swagger-ui.enabled=
springdoc.swagger-ui.path=

# 서버 설정
SERVER_PORT=
```

> ⚠ `.env` 파일은 `.gitignore`에 포함되어 있어 Git에 올라가지 않음.  
> 보안 강화를 위해 `POSTGRES_PASSWORD` 값은 안전하게 관리할 것.

---

### 2.2 Docker Compose 실행

아래 명령어를 사용하여 애플리케이션과 데이터베이스를 함께 실행할 수 있습니다.

```sh
docker-compose up --build
```

이후 컨테이너가 정상적으로 실행되는지 확인:

```sh
docker ps
```

컨테이너를 종료하려면:

```sh
docker-compose down
```

`-d` 옵션을 사용하면 백그라운드에서 실행되며, 로그 확인 시 `docker-compose logs -f` 사용 가능.


---

## 3. 주요 설정 파일 설명

### 3.1 init.sql (PostgreSQL 초기화 스크립트)

이 파일은 PostgreSQL 컨테이너가 처음 실행될 때 자동으로 실행됩니다.
```sh
DO $$ BEGIN
    CREATE ROLE <user name> WITH LOGIN PASSWORD '<user password>';
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'User already exists';
END $$;

DO $$ BEGIN
    CREATE DATABASE <database name> OWNER <database user>;
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'Database already exists';
END $$;

ALTER DATABASE rushcutter SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE rushcutter TO rushcutter_user;
```

PostgreSQL 초기 데이터베이스 및 사용자를 설정하는 역할을 합니다.
`.env` 파일의 값과 일치해야 합니다.

---

### 3.2 application.properties (Spring Boot 설정)

Spring Boot 애플리케이션 실행 시 사용할 환경 변수와 DB 연결 설정이 포함됩니다.

```properties
spring.application.name=rushcutter

# PostgreSQL
spring.datasource.url=${POSTGRES_URL}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Swagger UI
springdoc.api-docs.path=
springdoc.swagger-ui.path=
springdoc.api-docs.enabled=
springdoc.swagger-ui.enabled=
springdoc.swagger-ui.csrf.enabled=
```

## 4. Swagger UI 

Swagger를 사용하여 API를 테스트할 수 있습니다.

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다.

📌 **Swagger UI**: http://localhost:8080/swagger-ui

📌 **OpenAPI Docs**: http://localhost:8080/api-docs

---

## 5. 추가 명령어

- **로그 확인**

```sh
docker-compose logs -f
```

- **DB 컨테이너 접속 (PostgreSQL CLI 사용)**

```sh
docker exec -it <DB container name> psql -U <DB username> -d <DB name>
```

- **Gradle 빌드 실행**

```sh 
./gradlew clean build -x test
```

- **컨테이너 삭제 후 재배포**

```sh 
docker-compose down -v && docker-compose up -d --build
```

---
