package com.campushub.notice.config;

import com.campushub.common.mq.RabbitKeys;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    public TopicExchange noticeExchange() {
        return new TopicExchange(RabbitKeys.NOTICE_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadExchange() {
        return new TopicExchange(RabbitKeys.DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Queue smsSendQueue() {
        return QueueBuilder.durable(RabbitKeys.SMS_SEND_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Queue noticeSendQueue() {
        return QueueBuilder.durable(RabbitKeys.NOTICE_SEND_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitKeys.DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding smsBinding(Queue smsSendQueue, TopicExchange smsExchange) {
        return BindingBuilder.bind(smsSendQueue).to(smsExchange).with(RabbitKeys.SMS_SEND_KEY);
    }

    @Bean
    public Binding noticeBinding(Queue noticeSendQueue, TopicExchange noticeExchange) {
        return BindingBuilder.bind(noticeSendQueue).to(noticeExchange).with(RabbitKeys.NOTICE_SEND_KEY);
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                             MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
