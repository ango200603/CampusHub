package com.campushub.file.config;

import com.campushub.common.mq.RabbitKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public TopicExchange aiExchange() {
        return new TopicExchange(RabbitKeys.AI_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadExchange() {
        return new TopicExchange(RabbitKeys.DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Queue aiParseQueue() {
        return QueueBuilder.durable(RabbitKeys.AI_PARSE_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitKeys.DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding aiParseBinding(Queue aiParseQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(aiParseQueue).to(aiExchange).with(RabbitKeys.AI_PARSE_KEY);
    }

    @Bean
    public Binding deadBinding(Queue deadLetterQueue, TopicExchange deadExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadExchange).with(RabbitKeys.DEAD_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
