# 1. Build Stage: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.3-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 유지: 종속성 다운로드 속도를 높이기 위해 Gradle 캐시를 유지
VOLUME /root/.gradle

# Gradle Wrapper 및 종속성 캐싱을 위해 필요한 파일 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# 종속성 미리 다운로드하여 빌드 시간을 단축
RUN ./gradlew dependencies --no-daemon

# 전체 프로젝트를 컨테이너에 복사하고 빌드 (테스트 제외)
COPY . .
RUN ./gradlew clean build -x test --no-daemon --parallel

# 2. Run Stage: 애플리케이션 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일을 실행 환경으로 복사
COPY --from=builder /app/build/libs/rushcutter-0.0.1-SNAPSHOT.jar /app.jar

# 컨테이너 실행 시 JAR 파일을 실행하는 명령어 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]