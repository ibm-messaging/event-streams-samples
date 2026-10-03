FROM apache/kafka:latest

USER root

RUN addgroup --gid 5000 --system esgroup && \
    adduser --uid 5000 --ingroup esgroup --system esuser && \
    mkdir -p /opt/kafka/plugins /opt/kafka/logs && \
    chown -R esuser:esgroup /opt/kafka/plugins /opt/kafka/logs

COPY --chown=esuser:esgroup connectors /opt/kafka/plugins/

WORKDIR /opt/kafka

EXPOSE 8083

USER esuser

ENTRYPOINT ["bin/connect-distributed.sh", "config/connect-distributed.properties"]
