package config

import (

	"github.com/caarlos0/env"
)

type Config struct {
	KafkaEndpoints  []string `env:"KAFKA_ENDPOINTS" envSeparator:","`
	TopicName       string   `env:"TOPIC_NAME" envDefault:"kafka-golang-sample-topic"`
	APIKey          string   `env:"API_KEY"`
	MessageCount    int      `env:"MESSAGE_COUNT" envDefault:"100"`
	ToRun           string   `env:"TO_RUN" envDefault:"producer"`
}

// Read configuration from the environment.
func Read() (*Config, error) {
	cfg := &Config{}

	if err := env.Parse(cfg); err != nil {
		return nil, err
	}

	return cfg, nil
}
