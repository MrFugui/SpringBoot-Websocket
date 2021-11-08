package com.wangfugui.apprentice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wangfugui.apprentice.dao.mapper")
public class ApprenticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprenticeApplication.class, args);
    }

}
