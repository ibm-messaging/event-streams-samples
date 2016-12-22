/**
 * Copyright 2015-2016 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corp. 2015-2016
 */

var producer;
var exports = module.exports = {};

/**
 * Constructs a Kafka Producer and registers listeners on the most common events
 * 
 * @param {object} Kafka - an instance of the node-rdkafka module
 * @param {object} producer_opts - producer configuration
 * @param {string} topicName - name of the topic to produce to
 * @param {function} shutdown - shutdown function
  * @return {Producer} - the Kafka Producer instance
*/
exports.buildProducer = function(Kafka, producer_opts, topicName, shutdown) {
    // Create Kafka producer
    producer = new Kafka.Producer(producer_opts);

    // Register listener for debug information; only invoked if debug option set in driver_options
    producer.on('event.log', function(log) {
        console.log(log);
    });

    // Register error listener
    producer.on('error', function(err) {
        console.error('Error from producer:' + JSON.stringify(err));

    });

    // Register callback invoked when producer has connected
    producer.on('ready', function() {
        console.log('The producer has started');

        // request metadata for all topics
        producer.getMetadata({
            timeout: 10000
        }, 
        function(err, metadata) {
            if (err) {
                console.error('Error getting metadata: ' + JSON.stringify(err));
                shutdown(-1);
            } else {
                console.log('Producer obtained metadata: ' + JSON.stringify(metadata));
                var topicsByName = metadata.topics.filter(function(t) {
                    return t.name === topicName;
                });
                if (topicsByName.length === 0) {
                    console.error('ERROR - Topic ' + topicName + ' does not exist. Exiting');
                    shutdown(-1);
                }
            }
        });

        // Create a topic object for the Producer to allow passing topic settings
        var topicOpts = { 'request.required.acks': -1 };
        var topic = producer.Topic(topicName, topicOpts);
        console.log('Topic object created with opts ' + JSON.stringify(topicOpts));
        var counter = 0;

        // Synchronously wait for a response from Message Hub / Kafka on every message produced,
        // by wrapping the produce(..) call into a Promise and waiting on the delivery report.
        // For high throughput the delivery-report should be handled asynchronously.
        var producerLoop = function() {
            var timeout, interval;
            new Promise(function(resolve, reject) {
                // Complete the promise when we receive the delivery report back from the broker
                var deliveryReportListener = function(err, report) {
                    clearTimeout(timeout);
                    clearInterval(interval);
                    resolve(report);
                }
                producer.once('delivery-report', deliveryReportListener);
                var value = new Buffer('This is a test message #' + counter);
                var key = 'key';
                counter++;
                // If partition is set to null, the client will use the default partitioner to choose one.
                var partition = null;
                try {
                    producer.produce(topic, partition, value, key);
                    // Keep calling poll() to get delivery reports
                    interval = setInterval(function() {
                        producer.poll();
                    }, 100);
                    // Fail the promise if we don't receive a delivery report within 5 secs
                    timeout = setTimeout(function() {
                        producer.removeListener('delivery-report', deliveryReportListener);
                        reject(new Error('Timed out while waiting for delivery report for message "' + value.toString()+ '"'));
                    }, 5000);
                } catch (err) {
                    producer.removeListener('delivery-report', deliveryReportListener);
                    reject(err);
                }
            })
            .then(function(report) {
                console.log('Message produced, offset: ' + report.offset);
                // Short wait for flow control in this sample app
                setTimeout(producerLoop, 2000);
            })
            .catch(function (reason) {
                console.log('Error producing message: ' + reason);
                // Longer wait before retrying
                setTimeout(producerLoop, 5000);
            });
        }
        producerLoop();
    });
    return producer;
}
