import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.testcontainers.containers.KafkaContainer
import java.time.Duration
import java.util.*

class SunnyDayScenarioTest {

    private val kafka = KafkaContainer()

    private var application: Application? = null

    @BeforeEach
    fun setup() {
        kafka.start()
    }

    @AfterEach
    fun cleanup() {
        application?.close()
        kafka.stop()
    }

    @Test
    fun `should send events to kafka topic`() {
        // given
        val application = createApplication(kafka.bootstrapServers)
        createTopic(kafka.bootstrapServers)

        val event = "anyEventValue"
        val consumer = createKafkaConsumer(kafka.bootstrapServers)

        application.fireAndWaitForCommit(event)

        // when
        val events = consumer.poll(Duration.ofSeconds(3)).first()

        // then
        assertThat(events)
            .extracting { it.value() }
            .isEqualTo(event)
    }

    private fun createApplication(bootstrapServers: String): Application {
        val applicationFactory = ApplicationFactory()

        return applicationFactory.createApplication(bootstrapServers)
    }

    private fun createKafkaConsumer(bootstrapServers: String): KafkaConsumer<String, String> {
        val properties = Properties()

        properties["bootstrap.servers"] = bootstrapServers
        properties["max.poll.size"] = 1
        properties["group.id"] = "sunnyDayScenarioGroup"
        properties["max.metadata.age.ms"] = 1000
        properties["auto.offset.reset"] = "earliest"
        properties["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
        properties["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"

        val consumer = KafkaConsumer<String, String>(properties)

        consumer.subscribe(listOf("topic"))

        return consumer
    }

    private fun createTopic(bootstrapServers: String) {
        val properties =  Properties()
        properties["bootstrap.servers"] = bootstrapServers

        val adminClient = AdminClient.create(properties)
        adminClient.use {
            adminClient.createTopics(listOf(NewTopic("topic", 1, 1))).all().get()
        }
    }
}
