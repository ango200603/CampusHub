package com.campushub.auth.config;

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
 * Auth service configuration.
 */
@Configuration
public class RabbitConfig {
    /**
     * Declares the sms exchange bean.
     */
    @Bean
    public TopicExchange smsExchange() {
        return new TopicExchange(RabbitKeys.SMS_EXCHANGE, true, false);
    }

    /**
     * Declares the sms send queue bean.
     */
    @Bean
    public Queue smsSendQueue() {
        return QueueBuilder.durable(RabbitKeys.SMS_SEND_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    /**
     * Declares the sms send binding bean.
     */
    @Bean
    public Binding smsSendBinding(Queue smsSendQueue, TopicExchange smsExchange) {
        return BindingBuilder.bind(smsSendQueue).to(smsExchange).with(RabbitKeys.SMS_SEND_KEY);
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
