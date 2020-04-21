package admin

import (
	"fmt"
	"os"
	"time"

	"github.com/Shopify/sarama"
)

type (
	// clusterAdminInterface the functions we care about for admin operations
	clusterAdminInterface interface {
		CreateTopic(string, *sarama.TopicDetail, bool) error
		DescribeTopics(topics []string) ([]*sarama.TopicMetadata, error)
		DeleteTopic(string) error
		Close() error
	}

	// Interface the admin interface we provide to callers
	Interface interface {
		MaybeCreateTopic(topicName string, partitions int)
		Close() error
	}

	// Client concrete representation of Interface
	Client struct {
		clusterAdmin clusterAdminInterface
		retryDelay   time.Duration
		client       sarama.Client
	}
)

// NewAdmin create a new instance of admin, must call close when done
func NewAdmin(brokers []string, apikey string) (*Client, error) {
	saramaConfig := newSaramaConfig(apikey)
	client, err := sarama.NewClient(brokers, saramaConfig)
	if err != nil {
		return nil, err
	}
	clusterAdmin, err := sarama.NewClusterAdminFromClient(client)
	if err != nil {
		return nil, err
	}
	a := &Client{
		clusterAdmin: clusterAdmin,
		retryDelay:   5 * time.Second,
		client:       client,
	}
	return a, nil
}

// newSaramaConfig returns a new instance of a configuration object required to
// connect to Kafka internally. As the connection happens internally, there is
// no need to authenticate.
func newSaramaConfig(apikey string) *sarama.Config {
	host, _ := os.Hostname()
	id := "admin-" + host
	config := sarama.NewConfig()
	config.Version = sarama.V1_1_0_0
	config.ClientID = id
	config.Net.TLS.Enable = true
	config.Net.SASL.Enable = true
	config.Net.SASL.User = "token"
	config.Net.SASL.Password = apikey
	return config
}

// MaybeCreateTopic creates a topic with the specified name
// if it doesn't exist. If it does exist already, this function
// does not do anything.
// `partitions` is hard-coded to 1 (when this function is called
// from main()) in this sample app as the Lite plan currently
// supports only having one partition.
func (ac *Client) MaybeCreateTopic(topicName string, partitions int) error {
	// Return early if the topic already exists - we cannot simply call CreateTopic
	// and check for an 'ErrTopicAlreadyExists' error code because Kafka will return
	// 'ErrInvalidReplicationFactor' if there is insufficient Kafka brokers available
	// (even if the topic already exists).
	topicMetadata, err := ac.clusterAdmin.DescribeTopics([]string{topicName})
	if err != nil {
		return err
	}
	if len(topicMetadata) != 1 {
		return fmt.Errorf("unexpected number of topics: %d", len(topicMetadata))
	}
	if topicMetadata[0].Err == sarama.ErrNoError {
		// Topic already exists check partitions match wanted
		if len(topicMetadata[0].Partitions) == partitions {
			return nil
		}
		// The existing topic is not the right layout, delete and recreate
		err = ac.clusterAdmin.DeleteTopic(topicName)
		if err != nil {
			return err
		}
	}

	replicationFactor := int16(3)
	for i := 0; i < 5; i++ {
		err = ac.clusterAdmin.CreateTopic(topicName, &sarama.TopicDetail{
			NumPartitions:     int32(partitions),
			ReplicationFactor: replicationFactor,
		}, false)

		if err == nil {
			break
		}

		if err.(*sarama.TopicError).Err == sarama.ErrTopicAlreadyExists {
			time.Sleep(ac.retryDelay)
			continue
		}
		return err
	}

	return err
}

// Close the admin client
func (ac *Client) Close() error {
	return ac.clusterAdmin.Close()
}
