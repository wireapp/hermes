# Build backend step
FROM eclipse-temurin:17-jdk AS backend-build

ENV PROJECT_ROOT /src
WORKDIR ${PROJECT_ROOT}

# Copy gradle specification
COPY gradle ${PROJECT_ROOT}/gradle
COPY gradlew ${PROJECT_ROOT}/
# Make sure gradlew is executable
RUN chmod +x gradlew
# Download gradle
RUN ./gradlew --version
# Copy top level deps
COPY build.gradle.kts settings.gradle.kts ${PROJECT_ROOT}/
# download and cache dependencies
RUN ./gradlew resolveDependencies --no-daemon

# Copy project and build
COPY . ${PROJECT_ROOT}
# And build the backend
RUN ./gradlew distTar --no-daemon

# Extract executables
RUN mkdir -p /out && \
    tar -xvf build/distributions/app.tar --strip-components=1 -C "/out" && \
    chmod +x /out/bin/hermes

# And prepare non-root runtime
FROM eclipse-temurin:17-jre as runtime-base
LABEL description="Hermes - Wire MLS Backend"
LABEL project="wire:hermes"

# Setup app root
ENV APP_ROOT /app
WORKDIR "${APP_ROOT}"

# Prepare logging
ENV FILE_LOG_PATH=/var/log/wire
ENV PRODUCTION_LOGGING=true
ENV ENABLE_FILE_LOG=false
# Setup ports
ENV HERMES_PORT=8080
EXPOSE ${HERMES_PORT}
# And finally JVM
# use 256 MB at the start and set cap to 2GB for heap
# this is automatically propagated to the launch script
ENV JAVA_OPTS="-Xms256m -Xmx2g"

# Set user and group
ARG uname=be
ARG gname=be
ARG uid=1001
ARG gid=1001

# Now let the user own all required folders
RUN groupadd -g ${gid} ${gname} && \
    useradd -u ${uid} -g ${gname} ${uname} && \
    mkdir -p "${FILE_LOG_PATH}" "${APP_ROOT}" && \
    chown -R ${uid}:${gid} "${FILE_LOG_PATH}" "${APP_ROOT}"

# Switch to user
USER ${uid}:${gid}

# And now final ready runtime
FROM runtime-base as runtime

# Copy backend
COPY --from=backend-build --chown=${uid}:${gid} /out "${APP_ROOT}/run"

# Set version
ARG release_version=development
ENV HERMES_VERSION=${release_version}

ENTRYPOINT ["/bin/sh"]
CMD ["-c", "/app/run/bin/hermes"]
