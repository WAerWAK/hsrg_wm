package com.waerwak;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.waerwak.mapper")
public class HsrgWmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsrgWmApplication.class, args);
    }

}
