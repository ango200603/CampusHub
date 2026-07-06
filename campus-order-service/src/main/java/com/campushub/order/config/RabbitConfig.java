package com.campushub.order.config;

import com.campushub.common.mq.RabbitKeys;
import java.util.Map;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(RabbitKeys.ORDER_EXCHANGE, true, false);
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
    public Queue orderPaySuccessQueue() {
        return QueueBuilder.durable(RabbitKeys.ORDER_PAY_SUCCESS_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(RabbitKeys.ORDER_TIMEOUT_QUEUE)
                .deadLetterExchange(RabbitKeys.DEAD_EXCHANGE)
                .deadLetterRoutingKey(RabbitKeys.DEAD_KEY)
                .build();
    }

    @Bean
    public Queue orderTimeoutDelayQueue() {
        return QueueBuilder.durable(RabbitKeys.ORDER_TIMEOUT_DELAY_QUEUE)
                .withArguments(Map.of(
                        "x-message-ttl", 900000,
                        "x-dead-letter-exchange", RabbitKeys.ORDER_EXCHANGE,
                        "x-dead-letter-routing-key", RabbitKeys.ORDER_TIMEOUT_KEY
                ))
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitKeys.DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding paySuccessBinding(Queue orderPaySuccessQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderPaySuccessQueue).to(orderExchange).with(RabbitKeys.ORDER_PAY_SUCCESS_KEY);
    }

    @Bean
    public Binding timeoutBinding(Queue orderTimeoutQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderTimeoutQueue).to(orderExchange).with(RabbitKeys.ORDER_TIMEOUT_KEY);
    }

    @Bean
    public Binding timeoutDelayBinding(Queue orderTimeoutDelayQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderTimeoutDelayQueue).to(orderExchange).with(RabbitKeys.ORDER_TIMEOUT_DELAY_KEY);
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
