package com.campushub.ai.config;

import com.campushub.ai.service.AiTaskService;
import com.campushub.common.mq.RabbitKeys;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiParseListener {
    private final AiTaskService aiTaskService;

    @RabbitListener(queues = RabbitKeys.AI_PARSE_QUEUE)
    public void onMessage(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        aiTaskService.consumeParseMessage(payload, message, channel);
    }
}
