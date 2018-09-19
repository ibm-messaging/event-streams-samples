"""
 Copyright 2015-2018 IBM

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
 © Copyright IBM Corp. 2015-2018
"""
import asyncio
from confluent_kafka import Consumer

class ConsumerTask(object):

    def __init__(self, conf, topic_name):
        self.consumer = Consumer(conf)
        self.topic_name = topic_name
        self.running = True

    def stop(self):
        self.running = False

    @asyncio.coroutine
    def run(self):
        print('The consumer has started')
        self.consumer.subscribe([self.topic_name])
        while self.running:
            msg = self.consumer.poll(1)
            if msg is not None and msg.error() is None:
                print('Message consumed: topic={0}, partition={1}, offset={2}, key={3}, value={4}'.format(
                    msg.topic(),
                    msg.partition(),
                    msg.offset(),
                    msg.key().decode('utf-8'),
                    msg.value().decode('utf-8')))
            else:
                print('No messages consumed')
                yield from asyncio.sleep(2)
        self.consumer.unsubscribe()
        self.consumer.close()

