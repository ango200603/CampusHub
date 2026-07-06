package com.campushub.notice.service;

import com.campushub.notice.vo.NoticeVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;

public interface NoticeService {
    List<NoticeVO> my(Long userId);

    void markRead(Long userId, Long id);

    void handleSms(Map<String, Object> payload, Message message, Channel channel) throws IOException;

    void handleNotice(Map<String, Object> payload, Message message, Channel channel) throws IOException;
}
