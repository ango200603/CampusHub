package com.campushub.ai.mq;

import com.campushub.ai.service.AiTaskService;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * Consumer facade for file parse messages.
 */
@Component
@RequiredArgsConstructor
public class FileParseConsumer {
    private final AiTaskService aiTaskService;

    /**
     * Consumes a file parse message.
     *
     * @param payload message body
     * @param message raw AMQP message
     * @param channel AMQP channel
     * @throws IOException when ack/nack fails
     */
    public void consume(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        aiTaskService.consumeParseMessage(payload, message, channel);
    }
}
