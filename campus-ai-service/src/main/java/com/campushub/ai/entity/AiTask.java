package com.campushub.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("ai_tasks")
public class AiTask {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long fileId;
    private String taskType;
    private String status;
    private String resultText;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
