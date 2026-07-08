package com.campushub.notice.service;

import com.campushub.notice.dto.NoticeCreateDTO;
import com.campushub.notice.vo.NoticeVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

/**
 * Notice service.
 */
public interface NoticeService {
    /**
     * Creates a notice.
     *
     * @param request create request
     * @return created notice
     */
    NoticeVO create(NoticeCreateDTO request);

    /**
     * Lists current user's notices.
     *
     * @param userId user id
     * @return notice list
     */
    List<NoticeVO> my(Long userId);

    /**
     * Marks a notice as read.
     *
     * @param userId user id
     * @param id notice id
     */
    void markRead(Long userId, Long id);

    /**
     * Handles mock SMS messages.
     *
     * @param payload message payload
     * @param message raw AMQP message
     * @param channel AMQP channel
     * @throws IOException when ack/nack fails
     */
    void handleSms(Map<String, Object> payload, Message message, Channel channel) throws IOException;

    /**
     * Handles notice messages.
     *
     * @param payload message payload
     * @param message raw AMQP message
     * @param channel AMQP channel
     * @throws IOException when ack/nack fails
     */
    void handleNotice(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
