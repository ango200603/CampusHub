package com.campushub.ai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.ai.dto.CreateAiTaskRequest;
import com.campushub.ai.entity.AiTask;
import com.campushub.ai.enums.AiTaskStatus;
import com.campushub.ai.mapper.AiTaskMapper;
import com.campushub.ai.service.AiTaskService;
import com.campushub.ai.vo.AiTaskVO;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Ai service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiTaskServiceImpl implements AiTaskService {
    private final AiTaskMapper aiTaskMapper;
    private final RabbitTemplate rabbitTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public AiTaskVO create(Long userId, CreateAiTaskRequest request) {
        AiTask task = newTask(userId, request.getFileId(), request.getTaskType());
        aiTaskMapper.insert(task);
        rabbitTemplate.convertAndSend(RabbitKeys.AI_EXCHANGE, RabbitKeys.AI_PARSE_KEY, Map.of(
                "taskId", task.getId(),
                "userId", userId,
                "fileId", request.getFileId(),
                "taskType", task.getTaskType()
        ));
        return AiTaskVO.from(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AiTaskVO get(Long userId, Long id) {
        AiTask task = aiTaskMapper.selectById(id);
        if (task == null || !task.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "AI 任务不存在");
        }
        return AiTaskVO.from(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AiTaskVO> my(Long userId) {
        return aiTaskMapper.selectList(Wrappers.<AiTask>lambdaQuery()
                        .eq(AiTask::getUserId, userId)
                        .orderByDesc(AiTask::getCreatedAt))
                .stream().map(AiTaskVO::from).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void consumeParseMessage(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        Long taskId = toLong(payload.get("taskId"));
        AiTask task = null;
        try {
            if (taskId != null) {
                task = aiTaskMapper.selectById(taskId);
            }
            if (task == null) {
                task = newTask(toLong(payload.get("userId")), toLong(payload.get("fileId")),
                        String.valueOf(payload.getOrDefault("taskType", "OCR_SUMMARY")));
                aiTaskMapper.insert(task);
            }
            if (AiTaskStatus.SUCCESS.name().equals(task.getStatus())) {
                channel.basicAck(tag, false);
                return;
            }
            task.setStatus(AiTaskStatus.PROCESSING.name());
            task.setUpdatedAt(LocalDateTime.now());
            aiTaskMapper.updateById(task);
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
            task.setStatus(AiTaskStatus.SUCCESS.name());
            task.setResultText(mockResult(payload));
            task.setErrorMessage(null);
            task.setUpdatedAt(LocalDateTime.now());
            aiTaskMapper.updateById(task);
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("AI parse failed, payload={}", payload, ex);
            if (task != null) {
                task.setStatus(AiTaskStatus.FAILED.name());
                task.setErrorMessage(ex.getMessage());
                task.setUpdatedAt(LocalDateTime.now());
                aiTaskMapper.updateById(task);
            }
            channel.basicNack(tag, false, false);
        }
    }

    private AiTask newTask(Long userId, Long fileId, String taskType) {
        if (userId == null || fileId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "AI 任务缺少 userId 或 fileId");
        }
        LocalDateTime now = LocalDateTime.now();
        AiTask task = new AiTask();
        task.setUserId(userId);
        task.setFileId(fileId);
        task.setTaskType(taskType == null ? "OCR_SUMMARY" : taskType);
        task.setStatus(AiTaskStatus.PENDING.name());
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return task;
    }

    private String mockResult(Map<String, Object> payload) {
        String name = String.valueOf(payload.getOrDefault("originalName", "上传文件"));
        return "{\"summary\":\"已完成 " + name + " 的模拟 OCR 和 AI 摘要。\","
                + "\"keywords\":[\"校园\",\"资料\",\"待办\"],"
                + "\"todos\":[\"复习摘要内容\",\"整理课程重点\",\"同步到个人任务清单\"]}";
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
