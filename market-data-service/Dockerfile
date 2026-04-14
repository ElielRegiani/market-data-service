# Multi-stage: Gradle builds inside the image (reproducible in CI). Java 21 matches the project toolchain.
# Optional — build the JAR on the host first, then point a separate image stage at build/libs/app.jar
# (same filename: app.jar from tasks.bootJar) if your pipeline prefers a cached compile step outside Docker.

# --- Build ---
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle/wrapper/ gradle/wrapper/
COPY build.gradle.kts settings.gradle.kts ./
RUN sed -i 's/\r$//' gradlew 2>/dev/null || true \
	&& chmod +x gradlew

COPY src/ src/

RUN ./gradlew bootJar -x test --no-daemon

# --- Runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system --gid 1001 app \
	&& useradd --system --uid 1001 --gid app --no-create-home --shell /usr/sbin/nologin app

COPY --from=builder --chown=app:app /workspace/build/libs/app.jar app.jar

USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
