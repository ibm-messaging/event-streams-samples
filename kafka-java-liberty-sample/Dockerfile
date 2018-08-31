FROM gradle:jdk9 as javabuild

USER root
COPY . /app
WORKDIR /app

RUN gradle --no-daemon assemble

FROM websphere-liberty:javaee8
COPY --from=javabuild /app/target/defaultServer/server.xml /config/server.xml
COPY --from=javabuild /app/target/defaultServer/apps/MessageHubLibertyApp.war /tmp
RUN unzip -q /tmp/MessageHubLibertyApp.war -d /opt/ibm/wlp/usr/servers/defaultServer/apps/MessageHubLibertyApp.war
