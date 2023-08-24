package com.sdu.base.common.config;

import com.sdu.base.common.annotation.TransactionalExecuteConfigurer;
import com.sdu.base.common.aspect.TransactionalExecuteAspect;
import com.sdu.base.common.dao.SecureInvokeRecordDAO;
import com.sdu.base.common.repository.TransactionalExecuteRecordMapper;
import com.sdu.base.common.service.transaction.MQProducer;
import com.sdu.base.common.service.transaction.TransactionalExecuteService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@MapperScan(basePackageClasses = TransactionalExecuteRecordMapper.class)
@Import({TransactionalExecuteAspect.class, SecureInvokeRecordDAO.class})
public class TransactionAutoConfiguration {

    @Nullable
    protected Executor executor;

    /**
     * Collect any beans through autowiring.
     */
    @Autowired
    void setConfigurers(ObjectProvider<TransactionalExecuteConfigurer> configurers) {
        Supplier<TransactionalExecuteConfigurer> configurer = SingletonSupplier.of(() -> {
            List<TransactionalExecuteConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one TransactionalExecuteConfigurer may exist");
            }
            return candidates.get(0);
        });
        executor = Optional.ofNullable(configurer.get()).map(TransactionalExecuteConfigurer::getSecureInvokeExecutor).orElse(ForkJoinPool.commonPool());
    }

    @Bean
    public TransactionalExecuteService getSecureInvokeService(SecureInvokeRecordDAO secureInvokeRecordDAO) {
        return new TransactionalExecuteService(secureInvokeRecordDAO, executor);
    }

    @Bean
    public MQProducer getMQProducer() {
        return new MQProducer();
    }
}
