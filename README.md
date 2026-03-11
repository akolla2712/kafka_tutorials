# Kafka Tutorials with Spring Boot

A comprehensive demonstration of Apache Kafka integration with Spring Boot, featuring producer and consumer examples with Docker containerization.

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Producer      │    │   Kafka Broker  │    │   Consumer      │
│   Service       │───▶│   (Docker)      │───▶│   Service       │
│                 │    │                 │    │                 │
│ • OrderService  │    │ • Topic: orders │    │ • Message       │
│ • KafkaTemplate │    │ • Port: 9092    │    │   Processing    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Project Structure

```
kafka-tutorials/
├── docker-compose.yaml          # Kafka broker configuration
├── pom.xml                      # Parent POM with modules
├── README.md                    # This documentation
├── kafak-producer/              # Producer microservice
│   ├── pom.xml
│   ├── src/main/java/com/kafaktutorials/
│   │   ├── ProducerExamplesApplication.java
│   │   ├── config/KafkaProducerConfig.java
│   │   └── service/OrderService.java
│   └── src/main/resources/application.properties
└── kafaka-consumer1/            # Consumer microservice
    ├── pom.xml
    └── src/main/java/com/kafaktutorials/
        └── ConsumerExamplesApplication.java
```

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker Desktop** with Docker Compose
- **Windows PowerShell** (for Windows users)

### 1. Start Kafka Broker

```powershell
# Navigate to project root
cd C:\Users\Anudeep\kafka\kafka-tutorials

# Start Kafka broker in Docker
docker-compose up -d
```

Verify Kafka is running:
```powershell
docker ps
```

### 2. Build the Project

```powershell
# Build all modules
mvn clean compile
```

### 3. Run Producer Service

```powershell
# Run the producer (sends messages to Kafka)
mvn spring-boot:run -pl kafak-producer
```

### 4. Run Consumer Service

```powershell
# Run the consumer (listens for messages)
mvn spring-boot:run -pl kafaka-consumer1
```

## ⚙️ Configuration

### Docker Configuration (`docker-compose.yaml`)

```yaml
version: '3.8'
services:
  kafka:
    image: apache/kafka:latest
    container_name: broker
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://localhost:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://host.docker.internal:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_NUM_PARTITIONS: 3
```

### Application Properties (`kafak-producer/src/main/resources/application.properties`)

```properties
# Kafka broker connection
kafka.bootstrap.server=host.docker.internal:9092

# Topic configuration
order_create_topic=orders
```

## 🔧 Key Components

### Producer Service (`OrderService.java`)

```java
@Service
public class OrderService implements CommandLineRunner {

  @Autowired
  KafkaTemplate kafkaTemplate;

  @Value("${order_create_topic}")
  private String topic;

  @Override
  public void run(String... args) throws Exception {
    String[] names = {
      "Aarav", "Priya", "Marcus", "Sofia", "Elijah", "Yuna", "Tariq", "Ingrid", "Luca", "Amara"
    };

    for (int i = 0; i < 10; i++) {
      kafkaTemplate.send(topic, "My Name is " + names[i]);
    }
  }
}
```

**Features:**
- Implements `CommandLineRunner` for automatic execution on startup
- Sends 10 sample messages to the `orders` topic
- Uses `KafkaTemplate` for simplified message publishing

### Producer Configuration (`KafkaProducerConfig.java`)

```java
@Configuration
public class KafkaProducerConfig {

  @Value("${kafka.bootstrap.server}")
  public String bootstrapServer;

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(properties);
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
```

**Configuration Details:**
- **Bootstrap Servers**: Connects to Kafka broker at `host.docker.internal:9092`
- **Key Serializer**: Uses `StringSerializer` for message keys
- **Value Serializer**: Uses `StringSerializer` for message values

## 🐛 Troubleshooting

### "node -1 being disconnected" Error

**Problem:** Producer cannot connect to Kafka broker.

**Solutions:**

1. **Check Docker Configuration:**
   ```yaml
   # Ensure advertised listeners use host.docker.internal
   KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://host.docker.internal:9092
   ```

2. **Update Application Properties:**
   ```properties
   # Use host.docker.internal instead of localhost
   kafka.bootstrap.server=host.docker.internal:9092
   ```

3. **Restart Kafka Container:**
   ```powershell
   docker-compose down
   docker-compose up -d
   ```

4. **Verify Port Accessibility:**
   ```powershell
   Test-NetConnection -ComputerName localhost -Port 9092
   ```

### Common Issues

- **Port 9092 not accessible**: Ensure Docker Desktop is running
- **Connection timeout**: Check firewall settings for port 9092
- **Container not starting**: Verify Docker resources allocation

## 📚 Dependencies

### Parent POM (`pom.xml`)

```xml
<properties>
    <java.version>17</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-kafka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-kafka-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Key Dependencies:**
- **Spring Boot Starter Kafka**: Core Kafka integration
- **Spring Boot Starter Kafka Test**: Testing utilities for Kafka

## 🔄 Message Flow

1. **Producer Startup**: `OrderService.run()` executes automatically
2. **Message Creation**: Generates sample messages with names
3. **Publish to Topic**: Messages sent to `orders` topic via `KafkaTemplate`
4. **Broker Processing**: Kafka broker receives and stores messages
5. **Consumer Processing**: Consumer service reads messages from topic

## 🚀 Development Notes

### Extending the Project

**Add More Producers:**
- Create additional services extending `CommandLineRunner`
- Configure different topics in `application.properties`

**Add Consumer Logic:**
- Implement `@KafkaListener` annotated methods
- Configure consumer groups and partitions

**Add Message Types:**
- Create custom POJO classes for structured data
- Configure appropriate serializers (JSON, Avro, etc.)

### Best Practices

- **Error Handling**: Implement proper exception handling for failed sends
- **Monitoring**: Add metrics and health checks
- **Configuration**: Use Spring profiles for different environments
- **Testing**: Write integration tests with embedded Kafka

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Verify Docker and Kafka configurations
3. Ensure all ports are accessible
4. Review application logs for detailed error messages

---

**Last Updated:** March 11, 2026
**Spring Boot Version:** 4.0.3
**Kafka Version:** Latest (Docker)
**Java Version:** 17</content>
<parameter name="filePath">C:\Users\Anudeep\kafka\kafka-tutorials\README.md
