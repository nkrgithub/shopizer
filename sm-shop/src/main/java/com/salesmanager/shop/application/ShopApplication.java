package com.salesmanager.shop.application;

import io.microsphere.spring.context.event.ParallelBeanFactoryListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ShopApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ShopApplication.class, ParallelBeanFactoryListener.class)
                .run(args);
    }

}
