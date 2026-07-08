package com.campushub.ai.service;

import com.campushub.ai.dto.CreateAiTaskRequest;
import com.campushub.ai.vo.AiTaskVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

/**
 * Ai service contract.
 */
public interface AiTaskService {
    /**
     * Creates a new record.
     */
    AiTaskVO create(Long userId, CreateAiTaskRequest request);

    /**
     * Returns a record by id.
     */
    AiTaskVO get(Long userId, Long id);

    /**
     * Returns records owned by the current user.
     */
    List<AiTaskVO> my(Long userId);

    /**
     * Consumes an asynchronous file parse message.
     */
    void consumeParseMessage(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
