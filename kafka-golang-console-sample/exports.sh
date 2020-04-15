export API_KEY="your-key"   # API key from the service credentials
export KAFKA_ENDPOINTS="your-kafka-endpoints"   # kafka_brokers_sasl from the credentials. 
                                                # must be formatted as "host:port,host2:port2"
export TO_RUN=producer      # decides on whether to run the producer or consumer
export TOPIC_NAME=kafka-golang-sample-topic     # name of the topic you want to produce to/consume from
export PARTITION_NUMBER=1   # number of partitions on the topic
export MESSAGE_COUNT=100    # number of messages to produce
