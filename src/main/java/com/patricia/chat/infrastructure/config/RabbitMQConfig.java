package com.patricia.chat.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "chat.exchange";
    public static final String ROUTING_KEY = "chat.notification";

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(EXCHANGE);
    }
}
