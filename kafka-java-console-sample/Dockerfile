FROM gradle:jdk9

USER root
COPY . /app
WORKDIR /app

RUN gradle -s --no-daemon downloadDependencies assemble

ENTRYPOINT ["java", "-jar", "/app/build/libs/kafka-java-console-sample-2.0.jar"]
