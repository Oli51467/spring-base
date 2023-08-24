package com.sdu.base.common.service.transaction;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sdu.base.common.dao.SecureInvokeRecordDAO;
import com.sdu.base.common.domain.po.TransactionalExecuteRecord;
import com.sdu.base.common.utils.JsonUtil;
import com.sdu.base.common.domain.dto.TransactionalExecuteDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Description: 安全执行处理器
 */
@Slf4j
@AllArgsConstructor
public class TransactionalExecuteService {

    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final SecureInvokeRecordDAO secureInvokeRecordDAO;

    private final Executor executor;

//    @Scheduled(cron = "*/100000000 * * * * ?")
//    public void retry() {
//        List<TransactionalExecuteRecord> secureInvokeRecords = secureInvokeRecordDAO.getWaitRetryRecords();
//        for (TransactionalExecuteRecord secureInvokeRecord : secureInvokeRecords) {
//            doAsyncInvoke(secureInvokeRecord);
//        }
//    }

    public void save(TransactionalExecuteRecord record) {
        secureInvokeRecordDAO.save(record);
    }

    private void retryRecord(TransactionalExecuteRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        TransactionalExecuteRecord update = new TransactionalExecuteRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setStatus(TransactionalExecuteRecord.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDAO.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {//或者可以采用退避算法
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);//重试时间指数上升 2m 4m 8m 16m
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDAO.removeById(id);
    }

    public void invoke(TransactionalExecuteRecord record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 非事务状态，直接执行，不做任何保证。
        if (!inTransaction) {
            return;
        }
        // 保存执行数据
        save(record);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                // 事务后执行
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }
        });
    }

    public void doAsyncInvoke(TransactionalExecuteRecord record) {
        executor.execute(() -> {
            System.out.println(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    public void doInvoke(TransactionalExecuteRecord record) {
        TransactionalExecuteDTO transactionalExecuteDTO = record.getTransactionalExecuteDTO();
        try {
            Class<?> beanClass = Class.forName(transactionalExecuteDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JsonUtil.toList(transactionalExecuteDTO.getParameterTypes(), String.class);
            List<Class<?>> parameterClasses = getParameters(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass, transactionalExecuteDTO.getMethodName(), parameterClasses.toArray(new Class[]{}));
            Object[] args = transactionalExecuteDTO.getArgs();
            // 执行方法
            method.invoke(bean, args);
            // 执行成功更新状态
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("TransactionalExecuteService invoke fail", e);
            // 执行失败，等待下次执行
            retryRecord(record, e.getMessage());
        }
    }

    @NotNull
    private List<Class<?>> getParameters(List<String> parameterStrings) {
        return parameterStrings.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("TransactionalExecuteService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
