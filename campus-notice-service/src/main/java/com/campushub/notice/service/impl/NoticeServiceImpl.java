package com.campushub.notice.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.notice.entity.Notice;
import com.campushub.notice.enums.ReadStatus;
import com.campushub.notice.mapper.NoticeMapper;
import com.campushub.notice.service.NoticeService;
import com.campushub.notice.vo.NoticeVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    @Override
    public List<NoticeVO> my(Long userId) {
        return noticeMapper.selectList(Wrappers.<Notice>lambdaQuery()
                        .eq(Notice::getUserId, userId)
                        .orderByDesc(Notice::getCreatedAt))
                .stream().map(NoticeVO::from).toList();
    }

    @Override
    public void markRead(Long userId, Long id) {
        int updated = noticeMapper.update(null, Wrappers.<Notice>lambdaUpdate()
                .eq(Notice::getId, id)
                .eq(Notice::getUserId, userId)
                .set(Notice::getReadStatus, ReadStatus.READ.code()));
        if (updated == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
    }

    @Override
    public void handleSms(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("模拟发送短信：手机号 {}，验证码 {}", payload.get("phone"), payload.get("code"));
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            channel.basicNack(tag, false, false);
        }
    }

    @Override
    public void handleNotice(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            Notice notice = new Notice();
            notice.setUserId(toLong(payload.get("userId")));
            notice.setTitle(String.valueOf(payload.getOrDefault("title", "系统通知")));
            notice.setContent(String.valueOf(payload.getOrDefault("content", "")));
            notice.setReadStatus(ReadStatus.UNREAD.code());
            notice.setCreatedAt(LocalDateTime.now());
            noticeMapper.insert(notice);
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("Notice message failed, payload={}", payload, ex);
            channel.basicNack(tag, false, false);
        }
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
