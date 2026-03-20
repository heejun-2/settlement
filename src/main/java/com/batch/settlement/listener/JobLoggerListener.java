package com.batch.settlement.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class JobLoggerListener {

    private static final String START_MESSAGE = "'{}' 배치를 시작합니다!";
    private static final String END_MESSAGE = "'{}' 배치가 종료되었습니다. (상태 : {})";

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("=================================");
        log.info(START_MESSAGE, jobExecution.getJobInstance().getJobName());
        log.info("=================================");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution){
        long duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        ).toMillis();
        log.info("=================================");
        log.info(END_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
        log.info("총 소요 시간: {} ms", duration);
        log.info("=================================");

        if(jobExecution.getStatus() == BatchStatus.FAILED){
            log.error("배치가 실패했습니다!");
        }
    }
}
