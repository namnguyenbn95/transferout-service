package vn.vnpay.dbinterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.dbinterface.repository.SmeCustomerUserRepository;

@Slf4j
@Service
public class SmeCustomerUserService {
    @Autowired
    private SmeCustomerUserRepository smeCustomerUserRepository;

    public SmeCustomerUser getCustomerByUsername(String username) {
        return smeCustomerUserRepository.findByUsername(username).orElse(null);
    }
}
