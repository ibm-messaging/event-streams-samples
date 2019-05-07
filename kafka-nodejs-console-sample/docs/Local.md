# IBM Event Streams for IBM Cloud Kafka Node.js console sample application: Local Development guide
As pushing the application into IBM Cloud® does not require you to build the application locally, this guide is here to guide you through the process, should you wish to build the application locally.

We will not discuss establishing a connection from your laptop to Event Streams for IBM Cloud. This is described in the [ connection guide](https://cloud.ibm.com/docs/services/EventStreams?topic=eventstreams-connecting#connecting).

## Prerequisites
* [Node.js](https://nodejs.org/en/) 8.X LTS
* [node-gyp] (https://www.npmjs.com/package/node-gyp)

Node-rdkafka will build librdkafka automatically. You must ensure you have the dependencies listed below installed. For more details, see [librdakfka's instructions](../../docs/librdkafka.md).

##### Linux
* openssl-dev
* libsasl2-dev
* libsasl2-modules
* C++ toolchain

##### macOS 
* [Brew](http://brew.sh/)
* [Apple Xcode command line tools](https://developer.apple.com/xcode/)
* `openssl` via Brew
* Export `CPPFLAGS=-I/usr/local/opt/openssl/include` and `LDFLAGS=-L/usr/local/opt/openssl/lib`

## Installing dependencies
Run the following commands on your local machine, after the prerequisites for your environment have been completed:
```shell
npm install
```

## Running the Sample
Once built, to run the sample, execute the following command:
```shell
node app.js <kafka_brokers_sasl> <api_key> <ca_location>
```

To find the values for `<kafka_brokers_sasl>` and `<api_key>`, access your Event Streams instance in IBM Cloud®, go to the `Service Credentials` tab and select the `Credentials` you want to use.  If your user value is `token`, specify that with the password seperated by a `:`.

`<ca_location>` is the path where the trusted SSL certificates are stored on your machine and is therefore system dependent. 
For example:
* Ubuntu: /etc/ssl/certs
* RedHat: /etc/pki/tls/cert.pem
* macOS: /usr/local/etc/openssl/cert.pem from openssl installed by brew

__Note__: `<kafka_brokers_sasl>` must be a single string enclosed in quotes. For example: `"host1:port1,host2:port2"`. We recommend using all the Kafka hosts listed in the `Credentials` you selected.

Alternatively, you can run only the producer or only the consumer by respectively appending the switches `-producer` or `-consumer`  to the command above.

The sample will run indefinitely until interrupted. To stop the process, use `Ctrl+C`.