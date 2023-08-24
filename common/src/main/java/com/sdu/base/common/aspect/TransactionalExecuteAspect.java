package com.sdu.base.common.aspect;

import com.sdu.base.common.annotation.TransactionalExecute;
import com.sdu.base.common.domain.dto.TransactionalExecuteDTO;
import com.sdu.base.common.domain.po.TransactionalExecuteRecord;
import com.sdu.base.common.service.transaction.TransactionalExecuteService;
import com.sdu.base.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 安全执行切面
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)//确保最先执行
@Component
public class TransactionalExecuteAspect {

    @Autowired
    private TransactionalExecuteService transactionalExecuteService;

    @Around("@annotation(transactionalExecute)")
    public Object around(ProceedingJoinPoint joinPoint, TransactionalExecute transactionalExecute) throws Throwable {
        boolean async = transactionalExecute.async();
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 非事务状态，直接执行，不做任何保证。
        if (!inTransaction) {
            return joinPoint.proceed();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        TransactionalExecuteDTO dto = TransactionalExecuteDTO.builder()
                .args(joinPoint.getArgs())
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtil.toStr(parameters))
                .build();
        TransactionalExecuteRecord record = TransactionalExecuteRecord.builder()
                .transactionalExecuteDTO(dto)
                .maxRetryTimes(transactionalExecute.maxRetryTimes())
                .build();
        transactionalExecuteService.invoke(record, async);
        return null;
    }
}
