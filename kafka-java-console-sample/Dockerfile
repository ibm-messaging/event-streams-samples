FROM ibmjava:8-sdk

ENV target /usr/src/kafka-java-console-sample
WORKDIR $target

COPY ./gradle $target/gradle
COPY ./gradlew $target
COPY ./build.gradle $target

RUN ./gradlew downloadDependencies

COPY . $target/
RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/kafka-java-console-sample-2.0.jar"]

