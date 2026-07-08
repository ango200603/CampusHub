package com.campushub.order.config;

import com.campushub.common.mq.RabbitKeys;
import com.campushub.order.service.OrderService;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Order message component.
 */
@Component
@RequiredArgsConstructor
public class OrderMessageListener {
    private final OrderService orderService;

    /**
     * Consumes payment success messages.
     */
    @RabbitListener(queues = RabbitKeys.ORDER_PAY_SUCCESS_QUEUE)
    public void paySuccess(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        orderService.handlePaySuccess(payload, message, channel);
    }

    /**
     * Consumes order timeout messages.
     */
    @RabbitListener(queues = RabbitKeys.ORDER_TIMEOUT_QUEUE)
    public void timeout(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        orderService.handleTimeout(payload, message, channel);
    }
}
