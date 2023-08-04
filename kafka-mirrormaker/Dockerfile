FROM alpine as builder

RUN apk update
RUN apk --no-cache add curl

RUN curl -L "https://downloads.apache.org/kafka/3.4.1/kafka_2.12-3.4.1.tgz" -o kafka.tgz
RUN mkdir /opt/kafka \
    && tar -xf kafka.tgz -C /opt/kafka --strip-components=1

FROM ibmjava:11

RUN addgroup --gid 5000 --system esgroup && \
    adduser --uid 5000 --ingroup esgroup --system esuser

COPY --chown=esuser:esgroup --from=builder /opt/kafka/bin/ /opt/kafka/bin/
COPY --chown=esuser:esgroup --from=builder /opt/kafka/libs/ /opt/kafka/libs/
COPY --chown=esuser:esgroup --from=builder /opt/kafka/config/ /opt/kafka/config/
RUN mkdir /opt/kafka/logs && chown esuser:esgroup /opt/kafka/logs

COPY --chown=esuser:esgroup entrypoint.sh /opt/kafka

WORKDIR /opt/kafka

ENV TOPIC_REGEX=.*

USER esuser

ENTRYPOINT ["./entrypoint.sh"]
