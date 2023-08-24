package com.sdu.base.common.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdu.base.common.domain.po.TransactionalExecuteRecord;
import com.sdu.base.common.repository.TransactionalExecuteRecordMapper;
import com.sdu.base.common.service.transaction.TransactionalExecuteService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SecureInvokeRecordDAO extends ServiceImpl<TransactionalExecuteRecordMapper, TransactionalExecuteRecord> {

    public List<TransactionalExecuteRecord> getWaitRetryRecords() {
        Date now = new Date();
        //查2分钟前的失败数据。避免刚入库的数据被查出来
        DateTime afterTime = DateUtil.offsetMinute(now, (int) TransactionalExecuteService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery()
                .eq(TransactionalExecuteRecord::getStatus, TransactionalExecuteRecord.STATUS_WAIT)
                .lt(TransactionalExecuteRecord::getNextRetryTime, new Date())
                .lt(TransactionalExecuteRecord::getCreateTime, afterTime)
                .list();
    }
}
