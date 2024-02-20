# kafka-topic-stats
Prints usage and topic configuration for [Event Streams](https://cloud.ibm.com/catalog/services/event-streams) topic partitions.

## Building
This project requires Java 11.

```
./gradle jar
```

## Running
```
export API_KEY=your api key here
export BOOTSTRAP_ENDPOINTS=kafka-0.example.org:9093,kafka-1.example.org:9093
java -jar ./build/libs/kafka-topic-stats.jar
```

## Example output
```
Topic Name, Partition ID, Used Bytes, retention.bytes, segment.bytes, cleanup.policy, retention.ms
mytopic, 0, 0, 1073741824, 536870912, delete, 86400000
mytopic, 1, 0, 1073741824, 536870912, delete, 86400000
mytopic, 2, 0, 1073741824, 536870912, delete, 86400000
```
