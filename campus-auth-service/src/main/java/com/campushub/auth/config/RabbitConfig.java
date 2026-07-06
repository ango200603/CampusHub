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

@Configuration
public class RabbitConfig {
    @Bean
    public TopicExchange smsExchange() {
        return new TopicExchange(RabbitKeys.SMS_EXCHANGE, true, false);
    }

    @Bean
    public Queue smsSendQueue() {
        return QueueBuilder.durable(RabbitKeys.SMS_SEND_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Binding smsSendBinding(Queue smsSendQueue, TopicExchange smsExchange) {
        return BindingBuilder.bind(smsSendQueue).to(smsExchange).with(RabbitKeys.SMS_SEND_KEY);
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
