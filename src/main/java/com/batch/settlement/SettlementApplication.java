package com.batch.settlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling 해당 배치를 실행시키는 주체가 스프링일때만
@SpringBootApplication
public class SettlementApplication {

	public static void main(String[] args) {
//		SpringApplication.run(SettlementApplication.class, args);
		System.exit(SpringApplication.exit(SpringApplication.run(SettlementApplication.class, args)));
	}

}
