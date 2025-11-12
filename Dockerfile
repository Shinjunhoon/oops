# 1. 빌드 단계 (Build Stage): JAR 파일을 생성하는 역할
# OpenJDK 17 LTS 버전을 기반으로 빌드 환경을 설정합니다.
FROM eclipse-temurin:17-jdk-focal AS builder
WORKDIR /app

# Gradle Wrapper 파일과 설정 파일 복사
# 프로젝트 루트 디렉터리에 Gradle Wrapper 파일(gradlew)이 있다고 가정합니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# Gradle 실행 권한 부여
RUN chmod +x ./gradlew

# JAR 파일 빌드 (테스트는 실행하지 않음: -x test)
RUN ./gradlew bootJar -x test

# --- 2. 실행 단계 (Runtime Stage): 실제 서버를 구동하는 역할 ---
# 경량화된 JRE(Java Runtime Environment) 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
# 빌드 단계의 경로에서 생성된 JAR 파일을 app.jar로 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot의 기본 포트 8080을 컨테이너 외부에 노출
EXPOSE 8080

# 서버 실행 명령어 (컨테이너 시작 시 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]