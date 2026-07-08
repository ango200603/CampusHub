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

/**
 * File service configuration.
 */
@Configuration
public class RabbitConfig {
    /**
     * Declares the ai exchange bean.
     */
    @Bean
    public TopicExchange aiExchange() {
        return new TopicExchange(RabbitKeys.AI_EXCHANGE, true, false);
    }

    /**
     * Declares the dead exchange bean.
     */
    @Bean
    public TopicExchange deadExchange() {
        return new TopicExchange(RabbitKeys.DEAD_EXCHANGE, true, false);
    }

    /**
     * Declares the ai parse queue bean.
     */
    @Bean
    public Queue aiParseQueue() {
        return QueueBuilder.durable(RabbitKeys.AI_PARSE_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    /**
     * Declares the dead letter queue bean.
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitKeys.DEAD_LETTER_QUEUE).build();
    }

    /**
     * Declares the ai parse binding bean.
     */
    @Bean
    public Binding aiParseBinding(Queue aiParseQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(aiParseQueue).to(aiExchange).with(RabbitKeys.AI_PARSE_KEY);
    }

    /**
     * Declares the dead binding bean.
     */
    @Bean
    public Binding deadBinding(Queue deadLetterQueue, TopicExchange deadExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadExchange).with(RabbitKeys.DEAD_KEY);
    }

    /**
     * Declares the message converter bean.
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Declares the rabbit template bean.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
