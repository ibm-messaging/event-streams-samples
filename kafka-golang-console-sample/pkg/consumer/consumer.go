package consumer

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/Shopify/sarama"
	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/config"
	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/producer"
)

type (
	// SeqConsumer represents the consumer that recieves the sequenced messages
	SeqConsumer struct {
		ConsumerGrp sarama.ConsumerGroup
		topicName   string
	}
)

// getConsumerConfig sets up the consumer configuration according to your application
func getConsumerConfig(apikey string) *sarama.Config {
	config := sarama.NewConfig()
	config.Version = sarama.V1_1_0_0
	config.Net.TLS.Enable = true
	config.Net.SASL.Enable = true
	config.Net.SASL.User = "token"
	config.Net.SASL.Password = apikey
	config.Consumer.Return.Errors = true
	config.Consumer.Offsets.CommitInterval = 250 * time.Millisecond
	config.Consumer.MaxWaitTime = 500 * time.Millisecond
	// start from oldest uncommitted offset
	config.Consumer.Offsets.Initial = sarama.OffsetOldest

	return config
}

// NewConsumer returns a new OrderedSeqConsumer
func NewConsumer(cfg *config.Config) (*SeqConsumer, error) {
	consumerConfig := getConsumerConfig(cfg.APIKey)
	consumer, err := sarama.NewConsumerGroup(cfg.KafkaEndpoints, "consumer-group", consumerConfig)
	if err != nil {
		return nil, err
	}
	sc := &SeqConsumer{
		ConsumerGrp: consumer,
		topicName:   cfg.TopicName,
	}
	return sc, nil
}

// Run starts the consumer
func (sc *SeqConsumer) Run() error {
	ctx, ctxCancel := context.WithCancel(context.Background())
	cHandler := consumerGroupHandler{
		// can set the number of messages to consumer here e.g.
		// toConsume: 50,
	}
	defer sc.ConsumerGrp.Close()
	fmt.Println("Waiting for messages to consume...")
	for {
		err := sc.ConsumerGrp.Consume(ctx, []string{sc.topicName}, cHandler)
		if err != nil {
			ctxCancel()
			sc.ConsumerGrp.Close()
			return err
		}
		ctxCancel()
		err = sc.ConsumerGrp.Close()
		if err == nil {
			break
		}
	}
	return nil
}

// consumerGroupHandler is used to handle individual topic/partition claims.
// It also provides hooks for your consumer group session life-cycle and allow you to
// trigger logic before or after the consume loop(s)
type consumerGroupHandler struct {
	// toConsume int64
}

// Setup is run at the beginning of a new session
func (c consumerGroupHandler) Setup(_ sarama.ConsumerGroupSession) error {
	return nil
}

// Cleanup is run at the end of a session, once all ConsumeClaim goroutines have exited
// but before the offsets are committed for the very last time.
func (c consumerGroupHandler) Cleanup(_ sarama.ConsumerGroupSession) error {
	return nil
}

func (c consumerGroupHandler) ConsumeClaim(s sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for msg := range claim.Messages() {
		// msgKey := string(msg.Key)
		payload := &producer.MessagePayload{}
		_ = json.Unmarshal(msg.Value, payload)
		fmt.Printf("Consumed message with value %v at offset %v\n", payload.MessageNumber, msg.Offset)
		s.MarkMessage(msg, "") // commit offset to mark the message as read
	}
	return nil
}
