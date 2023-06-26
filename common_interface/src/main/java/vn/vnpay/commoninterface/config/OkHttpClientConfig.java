package vn.vnpay.commoninterface.config;

import feign.RequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientConfig {

    @Value("${spring.application.name}")
    private String springAppName;

    @Bean
    public OkHttpClient okHttpClientFactoryConfig() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(200, 2L, TimeUnit.MINUTES)).connectTimeout(30L, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-B3-TraceId", MDC.get("traceId"));
            requestTemplate.header("X-B3-SpanId", MDC.get("spanId"));
            requestTemplate.header("Spring-Application-Name", springAppName);
        };
    }

}
