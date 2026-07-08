package com.campushub.file.mq;

import com.campushub.common.constant.MqConstant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer for file parse messages.
 */
@Component
@RequiredArgsConstructor
public class FileParseProducer {
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends a file parse message.
     *
     * @param userId user id
     * @param fileId file id
     * @param objectKey MinIO object key
     * @param originalName original file name
     * @param fileType coarse file type
     * @param taskType parse task type
     */
    public void send(Long userId, Long fileId, String objectKey, String originalName, String fileType, String taskType) {
        rabbitTemplate.convertAndSend(MqConstant.AI_EXCHANGE, MqConstant.AI_PARSE_KEY, Map.of(
                "userId", userId,
                "fileId", fileId,
                "objectKey", objectKey,
                "originalName", originalName,
                "fileType", fileType,
                "taskType", taskType
        ));
    }
}
