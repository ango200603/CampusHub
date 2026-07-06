package com.campushub.pay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.common.util.NoGenerator;
import com.campushub.pay.dto.PayCreateRequest;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.entity.PayRecord;
import com.campushub.pay.enums.PayStatus;
import com.campushub.pay.mapper.PayRecordMapper;
import com.campushub.pay.service.PayService;
import com.campushub.pay.vo.PayRecordVO;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PayRecordMapper payRecordMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public PayRecordVO create(PayCreateRequest request) {
        PayRecord existing = findByOrderNo(request.getOrderNo());
        if (existing != null) {
            return PayRecordVO.from(existing);
        }
        LocalDateTime now = LocalDateTime.now();
        PayRecord record = new PayRecord();
        record.setOrderNo(request.getOrderNo());
        record.setPayNo(NoGenerator.payNo());
        record.setAmount(request.getAmount());
        record.setStatus(PayStatus.WAITING.name());
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        payRecordMapper.insert(record);
        return PayRecordVO.from(record);
    }

    @Override
    public PayRecordVO mockSuccess(PayMockSuccessRequest request) {
        PayRecord record = null;
        if (StringUtils.hasText(request.getPayNo())) {
            record = payRecordMapper.selectOne(Wrappers.<PayRecord>lambdaQuery()
                    .eq(PayRecord::getPayNo, request.getPayNo())
                    .last("limit 1"));
        }
        if (record == null && StringUtils.hasText(request.getOrderNo())) {
            record = findByOrderNo(request.getOrderNo());
        }
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付单不存在");
        }
        if (!PayStatus.SUCCESS.name().equals(record.getStatus())) {
            int updated = payRecordMapper.update(null, Wrappers.<PayRecord>lambdaUpdate()
                    .eq(PayRecord::getId, record.getId())
                    .ne(PayRecord::getStatus, PayStatus.SUCCESS.name())
                    .set(PayRecord::getStatus, PayStatus.SUCCESS.name())
                    .set(PayRecord::getUpdatedAt, LocalDateTime.now()));
            if (updated > 0) {
                record.setStatus(PayStatus.SUCCESS.name());
                record.setUpdatedAt(LocalDateTime.now());
                rabbitTemplate.convertAndSend(RabbitKeys.ORDER_EXCHANGE, RabbitKeys.ORDER_PAY_SUCCESS_KEY, Map.of(
                        "orderNo", record.getOrderNo(),
                        "payNo", record.getPayNo(),
                        "amount", record.getAmount()
                ));
            }
        }
        return PayRecordVO.from(record);
    }

    @Override
    public PayRecordVO getByOrderNo(String orderNo) {
        PayRecord record = findByOrderNo(orderNo);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "支付记录不存在");
        }
        return PayRecordVO.from(record);
    }

    private PayRecord findByOrderNo(String orderNo) {
        return payRecordMapper.selectOne(Wrappers.<PayRecord>lambdaQuery()
                .eq(PayRecord::getOrderNo, orderNo)
                .last("limit 1"));
    }
}
