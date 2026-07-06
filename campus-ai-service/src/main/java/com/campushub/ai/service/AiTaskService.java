package com.campushub.ai.service;

import com.campushub.ai.dto.CreateAiTaskRequest;
import com.campushub.ai.vo.AiTaskVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

public interface AiTaskService {
    AiTaskVO create(Long userId, CreateAiTaskRequest request);

    AiTaskVO get(Long userId, Long id);

    List<AiTaskVO> my(Long userId);

    void consumeParseMessage(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
