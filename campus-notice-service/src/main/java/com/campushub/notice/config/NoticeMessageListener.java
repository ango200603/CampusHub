package com.campushub.notice.config;

import com.campushub.common.mq.RabbitKeys;
import com.campushub.notice.service.NoticeService;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeMessageListener {
    private final NoticeService noticeService;

    @RabbitListener(queues = RabbitKeys.SMS_SEND_QUEUE)
    public void sms(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        noticeService.handleSms(payload, message, channel);
    }

    @RabbitListener(queues = RabbitKeys.NOTICE_SEND_QUEUE)
    public void notice(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        noticeService.handleNotice(payload, message, channel);
    }
}
