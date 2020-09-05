import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.Network
import org.testcontainers.containers.ToxiproxyContainer

class ProxyAwareFailureScenarioTest {

    companion object {

        val network: Network = Network.newNetwork()

        val kafka = ProxyAwareKafkaContainer()
            .withExposedPorts(9093)
            .withNetwork(network)

        val toxiproxy: ToxiproxyContainer = ToxiproxyContainer()
            .withNetwork(network)

        lateinit var kafkaProxy: ToxiproxyContainer.ContainerProxy

        @BeforeAll
        @JvmStatic
        fun setupContainers() {
            toxiproxy.start()

            kafkaProxy = toxiproxy.getProxy(kafka, 9093)

            kafka.start()
        }

        private lateinit var application: Application

        @BeforeAll
        @JvmStatic
        fun setup() {
            application =
                createApplication()
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            application.close()
        }

        private fun createApplication(): Application {
            val applicationFactory = ApplicationFactory()

            val kafkaProxyIp = kafkaProxy.containerIpAddress
            val kafkaProxyPort = kafkaProxy.proxyPort

            return applicationFactory.createApplication("$kafkaProxyIp:$kafkaProxyPort")
        }
    }

    @Test
    fun `should throw exception when committing failed`() {
        // given
        val event = "anyEventValue12352634"

        // when
        assertThatCode { application.fireAndWaitForCommit(event) }.doesNotThrowAnyException()

        kafkaProxy.setConnectionCut(true)

        val exception = catchThrowable { application.fireAndWaitForCommit(event) }

        // then
        assertThat(exception).isInstanceOf(SendingFailedException::class.java)
    }
}
