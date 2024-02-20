package com.eventstreams.samples;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ReplicaInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.TopicConfig;

public class TopicStats {
    public static void main(String[] args) {
        final var apiKey = System.getenv("API_KEY");
        final var bootstrap = System.getenv("BOOTSTRAP_ENDPOINTS");

        var shouldExit = false;
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API_KEY environment variable is not set");
            shouldExit = true;
        }
        if (bootstrap == null || bootstrap.isEmpty()) {
            System.err.println("BOOTSTRAP_ENDPOINTS environment variable is not set");
            shouldExit = true;
        }
        if (shouldExit) {
            System.exit(1);
        }

        final var configs = new HashMap<String, Object>();
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configs.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        configs.put(SaslConfigs.SASL_JAAS_CONFIG,
            "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\""
            + apiKey + "\";");
        
        try (final AdminClient admin = AdminClient.create(configs)) {
            // Discover brokers
            final var nodes = admin.describeCluster().nodes().get();
            final var brokers = nodes.stream().map(v -> v.id()).collect(Collectors.toSet());

            // Get replica information for each broker and reduce into a map of TopicPartition -> ReplicaInfo.
            final var brokerToLogDir = admin.describeLogDirs(brokers).allDescriptions().get();
            final var logDirs = brokerToLogDir.values().stream().map(v -> v.values()).flatMap(Collection::stream).collect(Collectors.toSet());
            final var replicaInfo = logDirs.stream().map(v -> v.replicaInfos()).reduce((m1, m2) -> {
                var r = new HashMap<TopicPartition, ReplicaInfo>();
                r.putAll(m1); 
                // All of a partition's replicas should be about the same size. Be pessimistic for
                // the case that the partition is not fully in-sync, and pick the largest sized replica.
                m2.forEach((k, v) -> {
                    final var m1v = m1.get(k);
                    if (m1v == null || m1v.size() < v.size()) {
                        r.put(k, v);
                    }
                });
                return r;
            }).get();

            // Get all the topics, their descriptions, and their configuration
            final var topicNames = admin.listTopics().names().get();
            final var topicsDesc = admin.describeTopics(topicNames).all().get();
            final var topicsConfigResources = topicNames.stream().map(v -> new ConfigResource(Type.TOPIC, v)).collect(Collectors.toSet());
            final var topicsConfigs = admin.describeConfigs(topicsConfigResources).all().get();

            class PartitionInfo {
                final Config config;
                final long usedBytes;
                
                PartitionInfo(Config config, long usedBytes) {
                    this.config = config;
                    this.usedBytes = usedBytes;
                }
            }

            // Wraps TopicPartition in Comparable.
            class ComparableTopicPartition implements Comparable<ComparableTopicPartition> {
                final String topic;
                final int partition;

                ComparableTopicPartition(TopicPartition tp) {
                    this.topic = tp.topic();
                    this.partition = tp.partition();
                }

                @Override
                public int compareTo(ComparableTopicPartition o) {
                    var result = this.topic.compareTo(o.topic);
                    if (result == 0) { // topic names are the same, compare based on partition number
                        result = this.partition - o.partition;
                    }
                    return result;
                }
            }

            final var usageInfo = new TreeMap<ComparableTopicPartition, PartitionInfo>(); // TreeMap as it implements SortedMap.
            topicsConfigs.forEach((resource, config) -> {
                final var topicName = resource.name();
                final var topicDescription = topicsDesc.get(topicName);
                if (topicDescription == null) {
                    // skip if there isn't a description. The topic information is gathered
                    // at slightly different times, so it's possible there will be inconsistencies.
                    return;
                }
                for (final var partition : topicDescription.partitions()) {
                    final var tp = new TopicPartition(topicName, partition.partition());
                    final var info = replicaInfo.get(tp);
                    if (info == null) {
                        // skip if there isn't replica information. As per above, the data gathered
                        // might not be completely consistent.
                        continue;
                    }
                    final var usedBytes = info.size();
                    usageInfo.put(new ComparableTopicPartition(tp), new PartitionInfo(config, usedBytes));
                }
            });

            // Output in CSV format
            System.out.println("Topic Name, Partition ID, Used Bytes, retention.bytes, segment.bytes, cleanup.policy, retention.ms"); // column titles
            usageInfo.forEach((k, v) -> {
                System.out.printf("%s, %s, %d, %s, %s, %s, %s\n", 
                    k.topic,
                    k.partition,
                    v.usedBytes,
                    v.config.get(TopicConfig.RETENTION_BYTES_CONFIG).value(),
                    v.config.get(TopicConfig.SEGMENT_BYTES_CONFIG).value(),
                    v.config.get(TopicConfig.CLEANUP_POLICY_CONFIG).value(),
                    v.config.get(TopicConfig.RETENTION_MS_CONFIG).value());
            });

        } catch(final ExecutionException e) {
            // ExecutionException typically wraps the exception we *actually* care about...
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
}
