import mu.KLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.TimeUnit

class Application(private val kafkaProducer: KafkaProducer<String, String>) {

    fun fireAndWaitForCommit(value: String) {
        logger.info { "Fire and waiting for commit: $value" }

        val record = ProducerRecord("topic", "anyKey", value)

        val future = kafkaProducer.send(record)

        try {
            future.get(3, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.error(e) { "Could not produce event" }
            throw SendingFailedException(e)
        }
    }

    fun close() {
        kafkaProducer.close()
    }

    companion object: KLogging()
}
