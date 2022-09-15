# alura-kafka-studies

## Install
### Java 
Java 17 was used. You can download it [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or try the other version [here](https://www.oracle.com/java/technologies/java-se-glance.html).

### Maven
Apache Maven 3.8.6 was used. You can download it [here](https://maven.apache.org/download.cgi)

### Kafka
Download and install the latest Kafka version [here](https://kafka.apache.org/downloads).

Note: Version used at time was 3.2.1

Run commands in your Kafka installation folder (e.g ```kafka_2.13-3.2.1```)

## Run Zookeeper and Kafka locally

1. Start Zookeeper
   1.  ```bin/zookeeper-server-start.sh config/zookeeper.properties```
2. Start Kafka
    1. ```bin/kafka-server-start.sh config/server.properties```

## Kafka Topics
### Create
* Single partition and replication factor example:
  * ```bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic MY_TOPIC_NAME```

### List
* List all Kafka topics:
  * ```bin/kafka-topics.sh --list --bootstrap-server localhost:9092```

### Describe
* Describe all Kafka topics:
  * ```bin/kafka-topics.sh --describe --bootstrap-server localhost:9092```

### Edit Kafka topic
* Edit a Kafka topic (e.g change partitions to 3):
  * ```bin/kafka-topics.sh --alter --bootstrap-server localhost:9092  --topic MY_TOPIC_NAME --partitions 3```

## Producers and Consumers

### Produce
* Send a message into a topic:
    * ```bin/kafka-console-producer.sh --broker-list localhost:9092 --topic MY_TOPIC_NAME```

### Consume
* Consume messages from beginning of topic:
    * ```bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic MY_TOPIC_NAME --from-beginning```
* Or all new messages only:
    * ```bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic MY_TOPIC_NAME```

## Logs
Kafka and Zookeeper use its logs as records, so it's appropriate to change the default /tmp location:
* Configure kafka logs directory in ```config/server.properties```
* And Zookeeper in ```config/zookeeper.properties```

## Notes
* The max number of active consumers is determined by the number of partitions, because each consumer is going to read from a single partition.
* The message key determines to which partition the message will be written. Kafka applies a hash on this key.

## References

* Alura
  * [Kafka: Producers, Consumers & Streams](https://cursos.alura.com.br/course/kafka-introducao-a-streams-em-microservicos)