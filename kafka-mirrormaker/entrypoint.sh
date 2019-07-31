#!/bin/bash

./bin/kafka-mirror-maker.sh \
    --consumer.config config/source.properties \
    --producer.config config/destination.properties \
    --whitelist=${TOPIC_REGEX}
