package com.enchanted;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.enchanted.mapper")
public class LinkUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkUpApplication.class, args);
    }

}
