package producer

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/Shopify/sarama"
	"github.com/ZiadAbass/event-streams-samples/pkg/config"
)

type (
	kafkaProducer interface {
		SendMessage(msg *sarama.ProducerMessage) (partition int32, offset int64, err error)
		Close() error
	}

	//SeqProducer concrete ordered producer
	SeqProducer struct {
		Producer  kafkaProducer
		config    *config.Config
		topicName string
	}

	// MessagePayload the JSON representation of the produced messages
	MessagePayload struct {
		MessageNumber int    `json:"message_number"`
		Timestamp     string `json:"timestamp"`
	}
)

func getProducerConfig(apikey string) *sarama.Config {
	config := sarama.NewConfig()
	config.Version = sarama.V1_1_0_0
	config.Net.TLS.Enable = true
	config.Net.SASL.Enable = true
	config.Net.SASL.User = "token"
	config.Net.SASL.Password = apikey
	config.Producer.RequiredAcks = sarama.WaitForAll
	config.Producer.Return.Successes = true
	config.Producer.Retry.Max = 10
	return config
}

// NewProducer returns a new and configured SeqProducer
func NewProducer(cfg *config.Config) (*SeqProducer, error) {
	producerConfig := getProducerConfig(cfg.APIKey)
	producer, err := sarama.NewSyncProducer(cfg.KafkaEndpoints, producerConfig)
	if err != nil {
		return nil, err
	}
	sp := &SeqProducer{
		Producer:  producer,
		topicName: cfg.TopicName,
		config:    cfg,
	}
	return sp, nil
}

// Run runs the producer for sending configured messages
func (sp *SeqProducer) Run() error {
	fmt.Printf("Running the producer on topic `%v`\n", sp.config.TopicName)
	// Produce a sequence of messages
	for i := 0; i < sp.config.MessageCount; i++ {
		msg := sp.generateMessage(i)
		fmt.Printf("Producing message with value %v\n", i)
		_, _, err := sp.Producer.SendMessage(msg)
		if err != nil {
			return err
		}
		time.Sleep(time.Second * 1)
	}
	time.Sleep(time.Millisecond * 20)
	sp.Producer.Close()
	return nil
}

func Close() {
}

func (sp *SeqProducer) generateMessage(num int) *sarama.ProducerMessage {
	msg := &sarama.ProducerMessage{}
	msg.Topic = sp.topicName
	// We can use the manual partitioner to set the partitions:
	// msg.Partition = 1
	// We can also set the message key here like this:
	// msg.Key = 1
	t := time.Now()
	sequencedMessage := MessagePayload{
		MessageNumber: num,
		Timestamp:     t.String(),
	}
	jsonBytes, _ := json.Marshal(&sequencedMessage)
	msg.Value = sarama.ByteEncoder(jsonBytes)
	return msg
}
