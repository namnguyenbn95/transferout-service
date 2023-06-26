package vn.vnpay.commoninterface.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class OpenBankingConfig {
    @Value("${open-banking.app-id:DGI}")
    private String appId;

    @Value("${open-banking.service-code-enc:VCBF_ECD}")
    private String serviceCodeEnc;

    @Value("${open-banking.service-code-dec:VCBF_DCD}")
    private String serviceCodeDec;

    @Value("${open-banking.enc-url:http://sit-tibco-esb01:8280/t/vcb.com/api/1.0.0/customers/encrypt-data}")
    private String encUrl;

    @Value("${open-banking.dec-url:http://sit-tibco-esb01:8280/t/vcb.com/api/1.0.0/customers/decrypt-data}")
    private String decUrl;
}
