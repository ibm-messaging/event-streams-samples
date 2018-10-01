# How to build librdkafka for use with IBM Event Streams for IBM Cloud

[Librdkafka](https://github.com/edenhill/librdkafka) is a very good Kafka client written in C/C++. In fact, the clients we used in our Node.js and Python samples, node-rdkafka and confluent-kafka-python respectively, are actually languages bindings on top of librdkafka.

Librdkafka is very flexible but in order to be able to work with Event Streams for IBM Cloud it needs to be built with the correct dependencies.

## Linux dependencies
* openssl-dev
* libsasl2-dev
* libsasl2-modules
* C++ toolchain

## macOS dependencies
* [Brew](http://brew.sh/)
* [Apple Xcode command line tools](https://developer.apple.com/xcode/)
* `openssl` via Brew
* Export `CPPFLAGS=-I/usr/local/opt/openssl/include` and `LDFLAGS=-L/usr/local/opt/openssl/lib`
* Open Keychain Access, export all certificates in System Roots to a single .pem file

## Build and installation for confluent-kafka-python
Node-rdkafka will build librdkafka automatically while being installed, however confluent-kafka-python requires librdkafka to be installed manually.

Run the following commands:
* `git clone https://github.com/edenhill/librdkafka.git`
* `cd librdkafka`
* `git checkout v0.9.3`
* `./configure`

In the output, ensure that both libssl and libsasl2 were found. For example:

    checking for libssl (by pkg-config)... ok
    checking for libssl (by compile)... ok (cached)
    checking for libsasl2 (by pkg-config)... failed
    checking for libsasl2 (by compile)... ok (cached)

Also, ensure that librdkafka is configured to have SSL and SASL enabled:

    ENABLE_SSL               y
    ENABLE_SASL              y

* `make`
* `sudo make install`
