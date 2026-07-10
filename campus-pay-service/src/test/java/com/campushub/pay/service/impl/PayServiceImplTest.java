package com.campushub.pay.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.pay.dto.PayCreateRequest;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.entity.PayRecord;
import com.campushub.pay.enums.PayStatus;
import com.campushub.pay.mapper.PayRecordMapper;
import com.campushub.pay.vo.PayRecordVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class PayServiceImplTest {
    private static final String ORDER_NO = "CH202607082011009991234";
    private static final String PAY_NO = "PAY202607082012009991234";
    private static final BigDecimal AMOUNT = new BigDecimal("29.90");

    @Mock
    private PayRecordMapper payRecordMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PayServiceImpl payService;

    @BeforeAll
    static void initMyBatisPlusMetadata() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), PayRecord.class);
    }

    @Test
    void createShouldReturnExistingRecordWithoutInsertWhenOrderNoExists() {
        PayRecord existing = payRecord(PayStatus.WAITING);
        when(payRecordMapper.selectOne(any())).thenReturn(existing);

        PayRecordVO result = payService.create(PayCreateRequest.builder()
                .orderNo(ORDER_NO)
                .amount(AMOUNT)
                .build());

        assertEquals(ORDER_NO, result.getOrderNo());
        assertEquals(PAY_NO, result.getPayNo());
        assertEquals(PayStatus.WAITING.name(), result.getStatus());
        verify(payRecordMapper, never()).insert(any(PayRecord.class));
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    @Test
    void mockSuccessShouldUpdateWaitingRecordAndPublishPaySuccessMessage() {
        PayRecord waiting = payRecord(PayStatus.WAITING);
        when(payRecordMapper.selectOne(any())).thenReturn(waiting);
        when(payRecordMapper.update(eq(null), any())).thenReturn(1);

        PayRecordVO result = payService.mockSuccess(PayMockSuccessRequest.builder()
                .payNo(PAY_NO)
                .orderNo(ORDER_NO)
                .build());

        assertEquals(PayStatus.SUCCESS.name(), result.getStatus());
        verify(payRecordMapper).update(eq(null), any());
        verify(rabbitTemplate).convertAndSend(eq(RabbitKeys.ORDER_EXCHANGE), eq(RabbitKeys.ORDER_PAY_SUCCESS_KEY),
                argThat((Object payload) -> payload instanceof Map<?, ?> map
                        && ORDER_NO.equals(map.get("orderNo"))
                        && PAY_NO.equals(map.get("payNo"))
                        && AMOUNT.equals(map.get("amount"))));
    }

    @Test
    void mockSuccessShouldNotPublishAgainWhenRecordAlreadySuccess() {
        PayRecord success = payRecord(PayStatus.SUCCESS);
        when(payRecordMapper.selectOne(any())).thenReturn(success);

        PayRecordVO result = payService.mockSuccess(PayMockSuccessRequest.builder()
                .payNo(PAY_NO)
                .orderNo(ORDER_NO)
                .build());

        assertEquals(PayStatus.SUCCESS.name(), result.getStatus());
        verify(payRecordMapper, never()).update(eq(null), any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    private PayRecord payRecord(PayStatus status) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 8, 20, 12);
        return PayRecord.builder()
                .id(1L)
                .orderNo(ORDER_NO)
                .payNo(PAY_NO)
                .amount(AMOUNT)
                .status(status.name())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
