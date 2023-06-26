package vn.vnpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication(exclude = JacksonAutoConfiguration.class)
public class TransferoutServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferoutServiceApplication.class, args);
    }

}
