package main

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/ZiadAbass/event-streams-samples/pkg/admin"
	"github.com/ZiadAbass/event-streams-samples/pkg/config"
	"github.com/ZiadAbass/event-streams-samples/pkg/consumer"
	"github.com/ZiadAbass/event-streams-samples/pkg/producer"
	"github.ibm.com/mhub/mhlog"
)

var (
	logger = mhlog.NewConsoleLogger()
)

func main() {
	cfg, err := config.Read()
	if err != nil {
		logger.Log("error reading config: [error]", mhlog.ErrorKey, err)
		os.Exit(1)
	}
	switch cfg.ToRun {
	case "producer":
		a, err := admin.NewAdmin(cfg.KafkaEndpoints, cfg.APIKey)
		if err != nil {
			logger.Log("failure to create admin client: [error]", mhlog.ErrorKey, err)
			os.Exit(1)
		}
		err = a.MaybeCreateTopic(cfg.TopicName, cfg.PartitionNumber)
		if err != nil {
			logger.Log("failure to create topic: [error]", mhlog.ErrorKey, err)
			os.Exit(1)
		}
		p, err := producer.NewProducer(cfg)
		if err != nil {
			logger.Log("failure to create producer: [error]", mhlog.ErrorKey, err)
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
			logger.Log("failed to run the producer: [error]", mhlog.ErrorKey, err)
			os.Exit(1)
		}
	case "consumer":
		c, err := consumer.NewConsumer(cfg) //TODO:
		if err != nil {
			logger.Log("failure to create consumer: [error]", mhlog.ErrorKey, err)
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
			logger.Log("failed to run the consumer: [error]", mhlog.ErrorKey, err)
			os.Exit(1)
		}
	default:
		logger.Log("failed to run. `TO_RUN` env variable not set to `producer` nor `consumer`: [error]", mhlog.ErrorKey, err)
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
