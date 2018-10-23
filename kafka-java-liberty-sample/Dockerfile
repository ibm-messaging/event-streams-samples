FROM gradle:jdk8-alpine as jdk

COPY --chown=1000 . /usr/src/app
WORKDIR /usr/src/app

RUN gradle -s --no-daemon assemble

FROM websphere-liberty:javaee8

COPY --from=jdk /usr/src/app/target/defaultServer/apps/EventStreamsLibertyApp.war /tmp
COPY --from=jdk /usr/src//app/target/defaultServer/server.xml /config/server.xml

USER root

RUN unzip -q /tmp/EventStreamsLibertyApp.war \
      -d /opt/ibm/wlp/usr/servers/defaultServer/apps/EventStreamsLibertyApp.war \
  && mkdir -p /logs /opt/ibm/wlp/usr/servers/defaultServer/configDropins /opt/ibm/wlp/output/defaultServer/.classCache \
  && adduser --home /opt/ibm/wlp/usr/servers wlp --quiet --disabled-login --gecos "" --uid 1000 \
  && chown -R 1000:1000 /logs /opt/ibm/wlp/usr/servers /opt/ibm/wlp/output

USER wlp
