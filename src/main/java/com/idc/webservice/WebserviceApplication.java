package com.idc.webservice;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@Slf4j ---Lombok
public class WebserviceApplication {
    //1、声明日志记录器
//    static Logger logger = LoggerFactory.getLogger(WebserviceApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(WebserviceApplication.class, args);
    }

}
