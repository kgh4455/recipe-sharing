package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.mapper") 
public class FinalprojectApplication {
	public static void main(String[] args) {
		SpringApplication.run(FinalprojectApplication.class, args);
	}

}
