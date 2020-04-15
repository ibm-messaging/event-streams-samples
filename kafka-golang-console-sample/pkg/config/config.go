package config

import (
	"fmt"

	"github.com/caarlos0/env"
)

type Config struct {
	KafkaEndpoints  []string `env:"KAFKA_ENDPOINTS" envSeparator:","`
	TopicName       string   `env:"TOPIC_NAME" envDefault:"golang-sample"`
	APIKey          string   `env:"API_KEY"`
	PartitionNumber int      `env:"PARTITION_NUMBER" envDefault:"1"`
	MessageCount    int      `env:"MESSAGE_COUNT" envDefault:"100"`
	ToRun           string   `env:"TO_RUN" envDefault:"producer"`
}

// Read configuration from the environment.
func Read() (*Config, error) {
	cfg := &Config{}

	if err := env.Parse(cfg); err != nil {
		return nil, err
	}
	if cfg.PartitionNumber <= 0 {
		return nil, fmt.Errorf("Partition number is not valid")
	}

	return cfg, nil
}
