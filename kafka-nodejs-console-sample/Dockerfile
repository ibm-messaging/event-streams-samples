# Dockerfile to run the sample under current Node LTS
#
# docker build . -t node-rdkafka
# docker run --rm -it -e VCAP_SERVICES=${VCAP_SERVICES} node-rdkafka
# OR
# docker run --rm -it node-rdkafka <kafka_brokers_sasl> <api_key> /etc/ssl/certs
#
FROM ubuntu:22.04

RUN  apt-get update -qqy \
  && apt-get install -y --no-install-recommends \
     build-essential \
     node-gyp \
     libssl-dev \
     liblz4-dev \
     libpthread-stubs0-dev \
     libsasl2-dev \
     libsasl2-modules \
     make \
     nodejs npm ca-certificates \
  && rm -rf /var/cache/apt/* /var/lib/apt/lists/*

WORKDIR /usr/src/app

COPY *.js *.json *.md /usr/src/app/

RUN npm install -d
ENV LD_LIBRARY_PATH=/usr/src/app/node_modules/node-rdkafka/build/deps
ENTRYPOINT [ "node", "app.js" ]
CMD [ "" ]
