# This dockerfile expects Connector jars to have been built under a `connectors` directory
#
FROM ibmcom/eventstreams-kafka-ce-icp-linux-amd64:2019.2.1-3a2f93e as builder

FROM ibmjava:11

RUN addgroup --gid 5000 --system esgroup && \
    adduser --uid 5000 --ingroup esgroup --system esuser

COPY --chown=esuser:esgroup --from=builder /opt/kafka/bin/ /opt/kafka/bin/
COPY --chown=esuser:esgroup --from=builder /opt/kafka/libs/ /opt/kafka/libs/
COPY --chown=esuser:esgroup --from=builder /opt/kafka/config/ /opt/kafka/config/
RUN mkdir /opt/kafka/logs && chown esuser:esgroup /opt/kafka/logs

COPY --chown=esuser:esgroup connectors /opt/connectors

WORKDIR /opt/kafka

EXPOSE 8083

USER esuser

ENTRYPOINT ["./bin/connect-distributed.sh", "config/connect-distributed.properties"]
