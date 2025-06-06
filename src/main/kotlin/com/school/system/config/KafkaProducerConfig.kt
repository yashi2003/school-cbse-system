package com.school.system.config

import com.school.system.dto.StudentOnboardingEvent
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig {

    private val logger = LoggerFactory.getLogger(KafkaProducerConfig::class.java)

    @Bean
    fun producerFactory(): ProducerFactory<String, StudentOnboardingEvent> {
        val bootstrapServers = "localhost:9092"
        logger.info("Setting up Kafka Producer Factory with broker: $bootstrapServers")

        val config = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, StudentOnboardingEvent> {
        logger.info("Creating KafkaTemplate bean for StudentOnboardingEvent")
        return KafkaTemplate(producerFactory())
    }
}
