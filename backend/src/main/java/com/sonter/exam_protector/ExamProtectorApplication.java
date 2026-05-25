package com.sonter.exam_protector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExamProtectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamProtectorApplication.class, args);
	}

}
