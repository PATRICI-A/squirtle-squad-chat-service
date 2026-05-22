package com.patricia.chat.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class ChatRabbitMQConfig {

    @Value("${rabbitmq.exchange.chat}")
    private String chatExchange;

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(chatExchange);
    }
}

