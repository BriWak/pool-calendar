# ============================================
# Stage 1: Build the application
# ============================================
FROM eclipse-temurin:11-jdk-jammy AS builder

# Install sbt
RUN apt-get update && apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | \
    tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | \
    gpg --dearmor -o /etc/apt/trusted.gpg.d/sbt.gpg && \
    apt-get update && apt-get install -y sbt

WORKDIR /app
COPY . .

# Build the application using sbt-native-packager
RUN sbt -J-Xmx1536m -J-Xss2m stage

# ============================================
# Stage 2: Create a custom minimal JRE
# ============================================
FROM eclipse-temurin:11-jdk-jammy AS jre-builder

# Build custom JRE with only the modules Play needs
RUN jlink \
    --add-modules java.base,java.logging,java.naming,java.sql,\
java.xml,java.desktop,java.management,java.security.jgss,\
java.instrument,jdk.unsupported,java.scripting,\
java.net.http,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# ============================================
# Stage 3: Final minimal runtime image
# ============================================
FROM debian:bookworm-slim

# Copy custom JRE
COPY --from=jre-builder /custom-jre /opt/java
ENV JAVA_HOME=/opt/java
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Create non-root user
RUN useradd -m -s /bin/bash appuser
USER appuser
WORKDIR /home/appuser

# Copy the staged application
COPY --from=builder --chown=appuser:appuser \
    /app/target/universal/stage /home/appuser/app

EXPOSE 9000

# Start with optimized JVM flags
CMD ["./app/bin/pool-calendar", \
    "-Dhttp.port=9000", \
    "-Dplay.http.secret.key=${APPLICATION_SECRET}", \
    "-Dconfig.resource=production.conf", \
    "-J-Xmx256m", "-J-Xms128m", \
    "-J-Xss512k", \
    "-J-XX:+UseG1GC", \
    "-J-XX:MaxGCPauseMillis=100", \
    "-J-XX:+UseStringDeduplication", \
    "-J-XX:+OptimizeStringConcat", \
    "-J-XX:MaxMetaspaceSize=128m", \
    "-Dpidfile.path=/dev/null"]
