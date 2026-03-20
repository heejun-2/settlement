package com.batch.settlement.job;

import com.batch.settlement.domain.Orders;
import com.batch.settlement.domain.Settlement;
import com.batch.settlement.listener.JobLoggerListener;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SettlementJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JobLoggerListener jobLoggerListener;


    @Bean
    public Job settlementJob(){
        return new JobBuilder("settlementJob", jobRepository)
                .listener(jobLoggerListener)
                .start(settlementStep())
                .build();
    }

    @Bean
    public Step settlementStep(){
        return new StepBuilder("settlementStep", jobRepository)
                .<Orders, Settlement>chunk(1000)
                .transactionManager(transactionManager)
                .reader(ordersReader(null))
                .processor(settlementItemProcessor())
                .writer(settlementWriter())
                .build();

    }


    // ItemReader
    @Bean
    @StepScope
    public JpaPagingItemReader<Orders> ordersReader(@Value("#{jobParameters['targetDate']}") String targetDate){
        log.info("[Reader] 정산 집계 대상 날짜 : {}", targetDate);

        return new JpaPagingItemReaderBuilder<Orders>()
                .name("ordersReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(1000)
                .queryString("SELECT o FROM Orders o WHERE o.orderDate = :targetDate ORDER BY o.id")
                .parameterValues(Collections.singletonMap("targetDate", LocalDate.parse(targetDate)))
                .build();
    }

    // ItemProcesser
    @Bean
    public ItemProcessor<Orders, Settlement> settlementItemProcessor(){
        return item -> {
            int fee = (int) (item.getAmount() * 0.03); // 수수료 3% 계산
            int settlement = item.getAmount() - fee;    // 정산금액 계산

            return new Settlement(item.getId(), item.getStoreName(), settlement, LocalDate.now());
        };
    }


    // ItemWriter
    @Bean
    public JpaItemWriter<Settlement> settlementWriter(){
        return new JpaItemWriterBuilder<Settlement>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
