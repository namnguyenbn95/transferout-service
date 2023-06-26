package vn.vnpay.commoninterface.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EndpointConfig implements WebMvcConfigurer {

    @Autowired
    private EndpointInterceptor endpointInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(endpointInterceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
