package com.sdu.base.common.annotation;

import org.springframework.lang.Nullable;

import java.util.concurrent.Executor;

public interface TransactionalExecuteConfigurer {

    /**
     * 返回一个线程池
     */
    @Nullable
    default Executor getSecureInvokeExecutor() {
        return null;
    }

}
