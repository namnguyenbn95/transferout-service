package vn.vnpay.commoninterface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.vnpay.commons.security.ib.IbSecurity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class ECDHLibConfig {

    final String PRIVATE_KEY_FILE = "config/default.key";

    @Bean
    public void loadECDHLib() {
        try {
            System.loadLibrary("ECDH-vcbsme-1.0.3");
            log.info("Load ECDH successfully!");
        } catch (Exception ex) {
            log.info("Load ECDH failed: ", ex);
        }
    }

    @Bean
    public void getDefaultPrivateKey() {
        try {
            Path path = Paths.get(PRIVATE_KEY_FILE);
            byte[] bytes = Files.readAllBytes(path);
            IbSecurity.loadServerPrivateKey(bytes);
            log.info("Load private key successfully from: {}", PRIVATE_KEY_FILE);
        } catch (Exception ex) {
            log.info("Load private key failed: ", ex);
        }
    }
}
