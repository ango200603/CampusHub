package com.campushub.ai.config;

import com.campushub.ai.mq.FileParseConsumer;
import com.campushub.common.mq.RabbitKeys;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ listener for file parse messages.
 */
@Component
@RequiredArgsConstructor
public class AiParseListener {
    private final FileParseConsumer fileParseConsumer;

    /**
     * Receives file parse messages.
     *
     * @param payload message body
     * @param message raw AMQP message
     * @param channel AMQP channel
     * @throws IOException when ack/nack fails
     */
    @RabbitListener(queues = RabbitKeys.AI_PARSE_QUEUE)
    public void onMessage(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        fileParseConsumer.consume(payload, message, channel);
    }
}
