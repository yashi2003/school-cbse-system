package com.school.system.config

import com.school.system.dto.StudentOnboardingEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka // Enables Kafka listener support for @KafkaListener annotations
@Configuration
class KafkaConsumerConfig {

    private val logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)

    @Bean
    fun consumerFactory(): ConsumerFactory<String, StudentOnboardingEvent> {
        val bootstrapServers = "localhost:9092"
        val groupId = "school-service"

        logger.info("Configuring Kafka Consumer Factory with broker: $bootstrapServers and group: $groupId")

        val config = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"  // Trust all packages for deserialization
        )

        return DefaultKafkaConsumerFactory(
            config,
            StringDeserializer(),
            JsonDeserializer(StudentOnboardingEvent::class.java, false)
        )
    }

    @Bean
    fun kafkaListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, StudentOnboardingEvent> {
        logger.info("Creating KafkaListenerContainerFactory for StudentOnboardingEvent")
        val factory = ConcurrentKafkaListenerContainerFactory<String, StudentOnboardingEvent>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}
