package vn.vnpay.dbinterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.entity.MbPkgServiceFee;
import vn.vnpay.dbinterface.entity.MbPkgServicePromFee;
import vn.vnpay.dbinterface.repository.MbPkgServiceFeeRepository;
import vn.vnpay.dbinterface.repository.MbPkgServicePromFeeRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class MbPkgServiceFeeService {

    @Autowired
    MbPkgServiceFeeRepository mbPkgServiceFeeRepository;

    @Autowired
    MbPkgServicePromFeeRepository mbPkgServicePromFeeRepository;

    /**
     * Get validate fee code
     *
     * @param pkgCode
     * @param serviceCode
     * @param authMethod
     * @param hour
     * @param amount
     * @return
     */
    public MbPkgServiceFee getValidatePkgFee(
            String pkgCode,
            String serviceCode,
            String authMethod,
            int hour,
            BigDecimal amount,
            String ccy) {
        List<MbPkgServiceFee> listFees =
                mbPkgServiceFeeRepository.findByPkgCodeAndServiceCodeAndStatusAndCcy(
                        pkgCode, serviceCode, "1", ccy);
        MbPkgServiceFee[] fee = new MbPkgServiceFee[2];
        fee[0] = null;
        fee[1] = null;
        listFees.forEach(
                f -> {
                    try {
                        if (f.getFromHour() <= hour
                                && f.getToHour() >= hour
                                && f.getFromAmount().compareTo(amount) <= 0
                                && f.getToAmount().compareTo(amount) >= 0) {
                            if (f.getMethodOtp().equals("0")) {
                                fee[0] = f;
                            } else if (f.getMethodOtp().equals(authMethod)) {
                                fee[1] = f;
                            }
                        }
                    } catch (Exception e) {
                        log.info("Error: ", e);
                    }
                });
        if (fee[0] != null) {
            return fee[0];
        } else {
            return fee[1];
        }
    }

    /**
     * Get validate from fee code
     *
     * @param promCode
     * @param serviceCode
     * @param authMethod
     * @param hour
     * @param amount
     * @return
     */
    public MbPkgServicePromFee getValidatePromFee(
            String promCode,
            String serviceCode,
            String authMethod,
            int hour,
            BigDecimal amount,
            String ccy) {
        List<MbPkgServicePromFee> listFees =
                mbPkgServicePromFeeRepository.findByPromCodeAndServiceCodeAndStatusAndCcy(
                        promCode, serviceCode, "1", ccy);
        MbPkgServicePromFee[] fee = new MbPkgServicePromFee[2];
        fee[0] = null;
        fee[1] = null;
        listFees.forEach(
                f -> {
                    try {
                        if (f.getFromHour() <= hour
                                && f.getToHour() >= hour
                                && f.getFromAmount().compareTo(amount) <= 0
                                && f.getToAmount().compareTo(amount) >= 0) {
                            if (f.getMethodOtp().equals("0")) {
                                fee[0] = f;
                            } else if (f.getMethodOtp().equals(authMethod)) {
                                fee[1] = f;
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
        if (fee[0] != null) {
            return fee[0];
        } else {
            return fee[1];
        }
    }
}
