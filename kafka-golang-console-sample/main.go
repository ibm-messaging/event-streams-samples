package main

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/admin"
	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/config"
	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/consumer"
	"github.com/ZiadAbass/event-streams-samples/kafka-golang-console-sample/pkg/producer"
)

func main() {
	cfg, err := config.Read()
	if err != nil {
		fmt.Printf("error reading config:  %v", err)
		os.Exit(1)
	}
	switch cfg.ToRun {
	case "producer":
		a, err := admin.NewAdmin(cfg.KafkaEndpoints, cfg.APIKey)
		if err != nil {
			fmt.Printf("failure to create admin client: %v", err)
			os.Exit(1)
		}
		err = a.MaybeCreateTopic(cfg.TopicName, 1)
		if err != nil {
			fmt.Printf("failure to create topic: %v", err)
			os.Exit(1)
		}
		p, err := producer.NewProducer(cfg)
		if err != nil {
			fmt.Printf("failure to create producer:  %v", err)
			os.Exit(1)
		}
		chan1 := make(chan os.Signal)
		signal.Notify(chan1, os.Interrupt, syscall.SIGTERM)
		go func() {
			<-chan1
			cleanupProducer(p)
			os.Exit(1)
		}()
		err = p.Run()
		if err != nil {
			fmt.Printf("failed to run the producer: %v", err)
			os.Exit(1)
		}
	case "consumer":
		c, err := consumer.NewConsumer(cfg)
		if err != nil {
			fmt.Printf("failure to create consumer:  %v", err)
			os.Exit(1)
		}
		chan2 := make(chan os.Signal)
		signal.Notify(chan2, os.Interrupt, syscall.SIGTERM)
		go func() {
			<-chan2
			cleanupConsumer(c)
			os.Exit(1)
		}()
		err = c.Run()
		if err != nil {
			fmt.Printf("failed to run the consumer:  %v", err)
			os.Exit(1)
		}
	default:
		fmt.Println("failed to run. `TO_RUN` env variable not set to `producer` nor `consumer`")
		os.Exit(1)
	}
}

func cleanupProducer(osp *producer.SeqProducer) {
	fmt.Printf("Closing the producer...\n")
	producer.Close()
	osp.Producer.Close()
}

func cleanupConsumer(osc *consumer.SeqConsumer) {
	fmt.Printf("Closing the consumer...\n")
	osc.ConsumerGrp.Close()
}
