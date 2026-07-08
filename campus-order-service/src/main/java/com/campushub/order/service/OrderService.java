package com.campushub.order.service;

import com.campushub.order.dto.CreateOrderRequest;
import com.campushub.order.dto.OrderQueryDTO;
import com.campushub.order.vo.OrderVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

/**
 * Order service contract.
 */
public interface OrderService {
    /**
     * Creates a new record.
     */
    OrderVO create(Long buyerId, CreateOrderRequest request);

    /**
     * Returns a record by id.
     */
    OrderVO get(Long userId, Long id);

    /**
     * Returns records owned by the current user.
     */
    List<OrderVO> my(Long userId);

    /**
     * Queries records by request parameters.
     *
     * @param query query parameters
     * @return matched records
     */
    List<OrderVO> query(OrderQueryDTO query);

    /**
     * Cancels a pending record.
     */
    void cancel(Long userId, Long id);

    /**
     * Handles a payment success message.
     */
    void handlePaySuccess(Map<String, Object> payload, Message message, Channel channel) throws IOException;

    /**
     * Handles an order timeout message.
     */
    void handleTimeout(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
