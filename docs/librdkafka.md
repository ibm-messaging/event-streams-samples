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
