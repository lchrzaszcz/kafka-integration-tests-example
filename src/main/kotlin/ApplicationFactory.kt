class ApplicationFactory {

    fun createApplication(bootstrapServers: String): Application {
        val kafkaClientFactory = KafkaClientFactory()

        val kafkaProducer = kafkaClientFactory.createKafkaProducer(bootstrapServers)

        return Application(kafkaProducer)
    }
}