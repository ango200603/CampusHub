package com.campushub.order.service;

import com.campushub.order.dto.CreateOrderRequest;
import com.campushub.order.vo.OrderVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

public interface OrderService {
    OrderVO create(Long buyerId, CreateOrderRequest request);

    OrderVO get(Long userId, Long id);

    List<OrderVO> my(Long userId);

    void cancel(Long userId, Long id);

    void handlePaySuccess(Map<String, Object> payload, Message message, Channel channel) throws IOException;

    void handleTimeout(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
