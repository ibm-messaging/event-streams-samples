# Dockerfile to run the sample under current Python 3
#
# docker build -t python-rdkafka
# docker run --rm -it -e VCAP_SERVICES=${VCAP_SERVICES} python-rdkafka
# OR
# docker run --rm -it python-rdkafka <kafka_brokers_sasl> <kafka_admin_url> <api_key> <ca_location>
#
FROM python:3.6-stretch

RUN  apt-get update -qqy \
  && apt-get install -y --no-install-recommends \
     build-essential \
     liblz4-dev \
     libpthread-stubs0-dev \
     libsasl2-dev \
     libsasl2-modules \
     libssl-dev \
     make \
     python3 \
  && curl -sL https://github.com/edenhill/librdkafka/archive/v0.11.4.tar.gz \
    | tar zx -C /usr/src --strip-components=1 \
  && cd /usr/src && ./configure --prefix=/usr && make && make install \
  && rm -rf /var/cache/apt/* /var/lib/apt/lists/*

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN pip install -r requirements/dev.txt

ENTRYPOINT [ "python3", "app.py" ]
CMD [ "" ]
