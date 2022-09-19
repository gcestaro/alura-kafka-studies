# alura-kafka-studies

## Install

### Java

Java 17 was used. You can download
it [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or try the
other version [here](https://www.oracle.com/java/technologies/java-se-glance.html).

### Maven

Apache Maven 3.8.6 was used. You can download it [here](https://maven.apache.org/download.cgi)

### Kafka

Download and install the latest Kafka version [here](https://kafka.apache.org/downloads).

Note: Version used at time was 3.2.1

Run commands in your Kafka installation folder (e.g ```kafka_2.13-3.2.1```)

## Run Zookeeper and Kafka locally

1. Start Zookeeper
    1. ```bin/zookeeper-server-start.sh config/zookeeper.properties```
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
    * ```bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic MY_TOPIC_NAME --partitions 3```

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

Kafka and Zookeeper use its logs as records, so it's appropriate to change the default /tmp
location:

* Configure kafka logs directory in ```config/server.properties```
* And Zookeeper in ```config/zookeeper.properties```

## Cluster

It's possible to run multiple Kafka instances pointing to the same Zookeeper, creating a Kafka
Cluster.
In order to do that manually:

1. Create another property file for the N Kafka instance (e.g. kafka2.properties). In this file:
    1. Change the broker id (e.g. ```broker.id=2```)
    2. If running all instances on your local machine, change port: (
       e.g. ```listeners=PLAINTEXT://:9092```)
    3. Change ```log.dirs``` to fit your new instance
    4. Change ```offsets.topic.replication.factor```
       and ```transaction.state.log.replication.factor``` to 3 as suggested in the comments
    5. Create a default replication factor greater than default 1 (
       e.g. ```default.replication.factor = 3```)

If needed, try stop all instances and clients, remove metadata and tmp files from ```log.dirs``` and
run again.

## Notes

* The max number of active consumers is determined by the number of partitions, because each
  consumer is going to read from a single partition.
* The message key determines to which partition the message will be written. Kafka applies a hash on
  this key.

## References

* Alura
    * [Kafka](https://cursos.alura.com.br/formacao-kafka)