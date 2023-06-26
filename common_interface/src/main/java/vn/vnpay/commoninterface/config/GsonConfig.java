package vn.vnpay.commoninterface.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Gson.class)
public class GsonConfig {

    @Bean
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapterFactory(new PostProcessingEnabler()).create();
    }

}
