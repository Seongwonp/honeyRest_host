# syntax=docker/dockerfile:1
# =============================================================================
# 호스트 관리자 앱 (honeyRest_host) 운영 이미지
#   빌드 컨텍스트 = 이 저장소 루트. 공유 도메인 모듈은 git submodule libs/honeyrest-user 에서 가져오므로
#   반드시 서브모듈을 받은 상태여야 한다:  git clone --recurse-submodules  또는  git submodule update --init --recursive
#   (settings.gradle 이 libs/honeyrest-user/honeyrest-domain/build.gradle 이 없으면 빌드를 중단한다)
#   - 1단계(build): JDK 17 + Gradle Wrapper 로 bootJar 생성 (테스트는 CI 에서 실행하므로 생략)
#   - 2단계(runtime): JRE 17 + 비루트 사용자로 실행
#   eclipse-temurin 은 arm64(aarch64) 이미지를 제공하므로 Oracle Cloud Ampere(ARM) VM 에서 그대로 빌드된다.
# =============================================================================

FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# 1 OCPU / 6GB 서버에서 직접 빌드하므로 Gradle 메모리를 보수적으로 제한한다.
ENV GRADLE_OPTS="-Xmx512m -Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx1g -Dorg.gradle.workers.max=2 -Dorg.gradle.welcome=never"

# 빌드 스크립트 먼저 (includeBuild 대상인 서브모듈의 settings/build 스크립트 포함)
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
COPY libs/honeyrest-user/settings.gradle libs/honeyrest-user/build.gradle libs/honeyrest-user/
COPY libs/honeyrest-user/honeyrest-domain/build.gradle libs/honeyrest-user/honeyrest-domain/build.gradle
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew \
 && test -f libs/honeyrest-user/honeyrest-domain/build.gradle \
 || { echo "서브모듈 libs/honeyrest-user 가 비어 있습니다: git submodule update --init --recursive" >&2; exit 1; }

COPY libs/honeyrest-user/honeyrest-domain libs/honeyrest-user/honeyrest-domain
COPY src src

# Gradle 캐시(~/.gradle)는 BuildKit 캐시 마운트로 빌드 간에 재사용한다 (사용자 API 이미지와 같은 캐시 id 공유)
RUN --mount=type=cache,target=/root/.gradle,id=honeyrest-gradle \
    ./gradlew --no-daemon bootJar -x test \
 && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \; \
 && test -s /workspace/app.jar

# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre AS runtime

# 업로드 볼륨(/app/uploads)을 사용자 API 컨테이너와 공유하므로 두 이미지가 같은 UID/GID 를 쓴다.
ARG APP_UID=10001
RUN groupadd --system --gid ${APP_UID} honeyrest \
 && useradd --system --uid ${APP_UID} --gid honeyrest --home-dir /app --shell /usr/sbin/nologin honeyrest \
 && mkdir -p /app/uploads /app/logs \
 && chown -R honeyrest:honeyrest /app

WORKDIR /app
COPY --from=build --chown=honeyrest:honeyrest /workspace/app.jar /app/app.jar

ENV TZ=Asia/Seoul \
    SPRING_PROFILES_ACTIVE=prod \
    APP_STORAGE_LOCAL_DIR=/app/uploads \
    JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

USER honeyrest
EXPOSE 8081

# exec: java 가 PID 1 이 되어 docker stop 의 SIGTERM 을 받고 graceful shutdown 한다
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
