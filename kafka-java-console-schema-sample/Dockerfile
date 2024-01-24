FROM gradle:jdk11-alpine as jdk

COPY --chown=1000 . /usr/src/app
WORKDIR /usr/src/app

COPY build.gradle settings.gradle /usr/src/app/
COPY src /usr/src/app/src

RUN gradle -s --no-daemon assemble

FROM openjdk:11

COPY --from=jdk /usr/src/app/build/libs/kafka-java-console-schema-sample-2.0.jar /usr/src/app/

USER 1000

ENTRYPOINT ["java", "-jar", "/usr/src/app/kafka-java-console-schema-sample-2.0.jar"]
