import org.testcontainers.containers.KafkaContainer

class ProxyAwareKafkaContainer: KafkaContainer() {

    override fun getBootstrapServers(): String {
        return String.format(
            "PLAINTEXT://%s:%s",
            ProxyAwareFailureScenarioTest.kafkaProxy.containerIpAddress,
            ProxyAwareFailureScenarioTest.kafkaProxy.proxyPort)
    }

}