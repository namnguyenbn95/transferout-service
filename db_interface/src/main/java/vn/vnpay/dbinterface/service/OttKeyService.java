package vn.vnpay.dbinterface.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.entity.OttKeyEntity;
import vn.vnpay.dbinterface.repository.OttKeyRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

@Service
public class OttKeyService {

    @Autowired
    OttKeyRepository ottKeyRepository;

    public String getKeyValue(String mobileNo) {
        Optional<OttKeyEntity> opt = ottKeyRepository.findByMobileNo(mobileNo);
        if (!opt.isPresent()) {
            OttKeyEntity key = new OttKeyEntity();
            key.setCreatedDate(new Timestamp(Calendar.getInstance().getTime().getTime()));
            key.setMobileNo(mobileNo);
            key.setKeyValue(genKey());
            ottKeyRepository.saveAndFlush(key);
            return key.getKeyValue();
        } else {
            return opt.get().getKeyValue();
        }
    }

    private String genKey() {
        int leftLimit = 48;
        int rightLimit = 122;

        int targetStringLength = 16;

        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }
}
