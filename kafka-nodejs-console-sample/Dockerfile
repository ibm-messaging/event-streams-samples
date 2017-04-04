# Dockerfile to run the sample under current Node LTS
#
# docker build -t node-rdkafka
# docker run --rm -it -e VCAP_SERVICES=${VCAP_SERVICES} node-rdkafka
# OR
# docker run --rm -it node-rdkafka <kafka_brokers_sasl> <kafka_admin_url> <api_key> <ca_location>
#
FROM node:boron

RUN  apt-get update -qqy \
  && apt-get install -y --no-install-recommends \
     build-essential \
     liblz4-dev \
     libpthread-stubs0-dev \
     libsasl2-dev \
     libsasl2-modules \
     libssl-dev \
     make \
     python \
  && rm -rf /var/cache/apt/* /var/lib/apt/lists/*

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN npm install

ENTRYPOINT [ "node", "app.js" ]
CMD [ "" ]
