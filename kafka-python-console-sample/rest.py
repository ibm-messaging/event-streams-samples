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
import requests

class EventStreamsRest(object):

    def __init__(self, rest_endpoint, api_key):
        self.path = '{0}/admin/topics'.format(rest_endpoint)
        self.headers = {
            'X-Auth-Token': api_key,
            'Content-Type': 'application/json'
        }

    def create_topic(self, topic_name, partitions=1, retention_hours=24):
        """
        POST /admin/topics
        """
        payload = {
            'name': topic_name,
            'partitions': partitions,
            'configs': {
                'retentionMs': retention_hours * 60 * 60 * 1000
            }
        }
        return requests.post(self.path, headers=self.headers, json=payload)

    def list_topics(self):
        """
        GET /admin/topics
        """
        return requests.get(self.path, headers=self.headers)
