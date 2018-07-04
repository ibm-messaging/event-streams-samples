"""
 Copyright 2015-2017 IBM

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Licensed Materials - Property of IBM
 © Copyright IBM Corp. 2015-2017
"""
import asyncio
import json
import os
import signal
import sys

import consumertask
import producertask
import rest

class MessageHubSample(object):

    def __init__(self, args):
        self.topic_name = 'kafka-python-console-sample-topic'
        self.opts = {}
        self.run_consumer = True
        self.run_producer = True
        self.consumer = None
        self.producer = None
        self.bluemix = True

        if os.environ.get('VCAP_SERVICES'):
            # Running in Bluemix
            print('Running in Bluemix mode.')
            vcap_services = json.loads(os.environ.get('VCAP_SERVICES'))
            for vcap_service in vcap_services:
                if vcap_service.startswith('messagehub'):
                    messagehub_service = vcap_services[vcap_service][0]
                    self.opts['brokers'] = ','.join(messagehub_service['credentials']['kafka_brokers_sasl'])
                    self.opts['api_key'] = messagehub_service['credentials']['api_key']
                    self.opts['username'] = messagehub_service['credentials']['user']
                    self.opts['password'] = messagehub_service['credentials']['password']
                    self.opts['rest_endpoint'] = messagehub_service['credentials']['kafka_admin_url']
            self.opts['ca_location'] = '/etc/ssl/certs'
        else:
            # Running locally on development machine
            self.bluemix = False
            print('Running in local mode.')

            if len(args) < 5:
                print('ERROR: It appears the application is running outside of Bluemix but the arguments are incorrect for local mode.')
                print('\nUsage:\npython {0} <kafka_brokers_sasl> <kafka_admin_url> {{ <api_key> | <user = token>:<password> }} <cert_location> [ -consumer | -producer ]\n'.format(args[0]))
                sys.exit(-1)

            self.opts['brokers'] = args[1]
            self.opts['rest_endpoint'] = args[2]
            if ":" in args[3]:
                credentials_list = args[3].split(":")
                self.opts['api_key'] = credentials_list[1]
                self.opts['username'] = credentials_list[0]
                self.opts['password'] = credentials_list[1]
            else:
                self.opts['api_key'] = args[3]
                self.opts['username'] = self.opts['api_key'][0:16]
                self.opts['password'] = self.opts['api_key'][16:48]

            # Bluemix/Ubuntu: '/etc/ssl/certs'
            # Red Hat: '/etc/pki/tls/cert.pem',
            # Mac OS X: select System root certificates from Keychain Access and export as .pem on the filesystem
            self.opts['ca_location'] = args[4]
            if not os.path.exists(self.opts['ca_location']):
                print('Error - Failed to access <cert_location> : {0}'.format(self.opts['ca_location']))
                sys.exit(-1)

            # In local mode the app can run only the producer or only the consumer
            if len(args) == 6:
                if args[5] == '-consumer':
                    self.run_producer = False
                if args[5] == '-producer':
                    self.run_consumer = False

        print('Kafka Endpoints: {0}'.format(self.opts['brokers']))
        print('Admin REST Endpoint: {0}'.format(self.opts['rest_endpoint']))

        if any(k not in self.opts for k in ('brokers', 'username', 'password', 'ca_location', 'rest_endpoint', 'api_key')):
            print('Error - Failed to retrieve options. Check that app is bound to a Message Hub service or that command line options are correct.')
            sys.exit(-1)

        # Use Message Hub's REST admin API to create the topic
        # with 1 partition and a retention period of 24 hours.
        rest_client = rest.MessageHubRest(self.opts['rest_endpoint'], self.opts['api_key'])
        print('Creating the topic {0} with Admin REST API'.format(self.topic_name))
        response = rest_client.create_topic(self.topic_name, 1, 24)
        print(response.text)

        # Use Message Hub's REST admin API to list the existing topics
        print('Admin REST Listing Topics:')
        response = rest_client.list_topics()
        print(response.text)

    def shutdown(self, signal, frame):
        print('Shutdown received.')
        if self.run_consumer:
            self.consumer.stop()
        if self.run_producer:
            self.producer.stop()

    @asyncio.coroutine
    def run_tasks(self):
        driver_options = {
            'bootstrap.servers': self.opts['brokers'],
            'security.protocol': 'SASL_SSL',
            'ssl.ca.location': self.opts['ca_location'],
            'sasl.mechanisms': 'PLAIN',
            'sasl.username': self.opts['username'],
            'sasl.password': self.opts['password'],
            'api.version.request': True,
            'broker.version.fallback': '0.10.2.1',
            'log.connection.close' : False
        }
        consumer_opts = {
            'client.id': 'kafka-python-console-sample-consumer',
            'group.id': 'kafka-python-console-sample-group'
        }
        producer_opts = {
            'client.id': 'kafka-python-console-sample-producer',
        }

        # Add the common options to consumer and producer
        for key in driver_options:
            consumer_opts[key] = driver_options[key]
            producer_opts[key] = driver_options[key]

        tasks = []
        # Start the clients
        if self.run_producer:
            self.producer = producertask.ProducerTask(self.bluemix, producer_opts, self.topic_name)
            tasks.append(asyncio.async(self.producer.run()))

        if self.run_consumer:
            self.consumer = consumertask.ConsumerTask(self.bluemix, consumer_opts, self.topic_name)
            tasks.append(asyncio.async(self.consumer.run()))

        done, pending = yield from asyncio.wait(tasks)
        for future in done | pending:
            future.result()
        sys.exit(0)

if __name__ == "__main__":
    app = MessageHubSample(sys.argv)
    signal.signal(signal.SIGINT, app.shutdown)
    signal.signal(signal.SIGTERM, app.shutdown)
    print('This sample app will run until interrupted.')
    sys.exit(asyncio.get_event_loop().run_until_complete(app.run_tasks()))
