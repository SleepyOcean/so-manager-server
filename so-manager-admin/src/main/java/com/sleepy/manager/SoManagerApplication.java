package com.sleepy.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SoManagerApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SoManagerApplication.class, args);
        System.out.println("so-manager启动成功\n");
    }
}
