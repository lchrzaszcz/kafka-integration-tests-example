import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*

class KafkaClientFactory {

    fun createKafkaProducer(bootstrapServers: String): KafkaProducer<String, String> {
        val properties = Properties()

        properties["bootstrap.servers"] = bootstrapServers
        properties["request.timeout.ms"] = 3000
        properties["delivery.timeout.ms"] = 3000
        properties["retries"] = 0
        properties["max.block.ms"] = 3000
        properties["linger.ms"] = 0
        properties["batch.size"] = 1
        properties["max.metadata.age.ms"] = 1000
        properties["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        properties["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

        return KafkaProducer(properties)
    }

}