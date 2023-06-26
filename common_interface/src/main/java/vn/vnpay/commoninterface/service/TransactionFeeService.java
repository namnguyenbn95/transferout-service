package vn.vnpay.commoninterface.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.response.ExchangeRateInquiryBankResponse;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.dto.FeeTransferDTO;
import vn.vnpay.commoninterface.dto.GetFeeTransferDTO;
import vn.vnpay.dbinterface.entity.MbAccountSerPkg;
import vn.vnpay.dbinterface.entity.MbPkgServiceFee;
import vn.vnpay.dbinterface.entity.MbPkgServicePromFee;
import vn.vnpay.dbinterface.repository.MbAccountSerPkgRepository;
import vn.vnpay.dbinterface.repository.MbPkgServicePromFeeRepository;
import vn.vnpay.dbinterface.service.MbPkgServiceFeeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionFeeService {
    @Autowired
    MbPkgServiceFeeService feeService;

    @Autowired
    Gson gson;

    @Autowired
    MbAccountSerPkgRepository accountSerPkgRepository;

    @Autowired
    MbPkgServicePromFeeRepository pkgServicePromFeeRepository;

    @Autowired
    CommonService commonService;

    public FeeTransferDTO getFeeTransfer(GetFeeTransferDTO input) {
        log.info("getFeeTransfer@input={}", gson.toJson(input));
        String accountPkgCode = input.getAccountPkgCode();
        String pkgCode = input.getPkgCode();
        String promCode = input.getPromCode();
        String serviceCode = input.getServiceCode();
        String authMethod = input.getAuthMethod();
        String ccyDebit = input.getCcy();
        BigDecimal amount = input.getAmount();
        BigDecimal exchangeAmount = input.getExchangeAmount();
        boolean isExamptVat = input.isExamptVat();
        String creditAccNo = input.getCreditAccount();
        String creditAccAlias = input.getCreditAccountAlias();
        List<String> creditAccLst = Arrays.asList(creditAccNo, creditAccAlias);
        String ccy = "VND".equals(ccyDebit) ? "VND" : "USD";
        if (StringUtils.isNotBlank(creditAccNo)) {
            FeeTransferDTO feeTransfer = commonService.checkCreditAccountFee(serviceCode, creditAccLst, ccy);
            if (null != feeTransfer) {
                return feeTransfer;
            }
        }
        int hour = Integer.parseInt(CommonUtils.TimeUtils.getNow("HH"));
        if (!Strings.isNullOrEmpty(accountPkgCode)) {
            Optional<MbAccountSerPkg> accountSerPkgOpt = accountSerPkgRepository.findByStatusAndPkgCode("1", accountPkgCode);
            if (accountSerPkgOpt.isPresent()) {
                String promCodeTemp = accountSerPkgOpt.get().getPromotionCode();
                List<MbPkgServicePromFee> listFees = pkgServicePromFeeRepository.findByPromCodeAndServiceCodeAndStatusAndCcy(promCodeTemp, serviceCode, "1", ccy);
                if (!listFees.isEmpty()) {
                    promCode = promCodeTemp;
                }
            }
        }
        log.info("getFeeTransfer@promCode:{}", promCode);
        if (!Strings.isNullOrEmpty(promCode)) {
            MbPkgServicePromFee promFee = feeService.getValidatePromFee(promCode, serviceCode, authMethod, hour, amount, ccy);
            log.info("promFee {}", promFee);
            if (promFee != null) {
                return makeFeeAndVat(exchangeAmount, promFee, ccy, isExamptVat, ccyDebit, input.getTiGiaUsd(), input.getTiGiaNgoaiTe());
            } else {
                promFee = feeService.getValidatePromFee(promCode, serviceCode, authMethod, hour, exchangeAmount, "VND");
                log.info("promFee {}", promFee);
                if (promFee != null) {
                    return makeFeeAndVat(exchangeAmount, promFee, "VND", isExamptVat, ccyDebit, input.getTiGiaUsd(), input.getTiGiaNgoaiTe());
                }
            }
        }
        MbPkgServiceFee fee = feeService.getValidatePkgFee(pkgCode, serviceCode, authMethod, hour, amount, ccy);
        if (fee != null) {
            log.info("fee by ccy");
            return makeFeeAndVat(amount, fee, ccy, isExamptVat, ccyDebit, input.getTiGiaUsd(), input.getTiGiaNgoaiTe());
        } else {
            fee = feeService.getValidatePkgFee(pkgCode, serviceCode, authMethod, hour, exchangeAmount, "VND");
            if (fee != null) {
                log.info("fee by vnd");
                return makeFeeAndVat(exchangeAmount, fee, "VND", isExamptVat, ccyDebit, input.getTiGiaUsd(), input.getTiGiaNgoaiTe());
            }
        }
        return new FeeTransferDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "VND");
    }

    private FeeTransferDTO makeFeeAndVat(BigDecimal amount, MbPkgServicePromFee promFee, String ccy, boolean isExamptVat,
                                         String ccyDebit, ExchangeRateInquiryBankResponse tiGiaUsd,
                                         ExchangeRateInquiryBankResponse tiGiaNgoaiTe) {
        log.info("makeFeeAndVat@" + gson.toJson(promFee));
//        log.info("amount {} ccy {} isExamptVat {} ccyDebit {} tiGiaUsd {} tiGiaNgoaiTe {}", amount,
//                ccy, isExamptVat, ccyDebit, gson.toJson(tiGiaUsd), gson.toJson(tiGiaNgoaiTe));
        try {
            int scale = "VND".equalsIgnoreCase(ccy) ? 0 : 2;
            BigDecimal feeN;
            if ("Percent".equals(promFee.getFeeType())) {
                feeN = promFee
                        .getFeeValue()
                        .multiply(amount)
                        .divide(new BigDecimal("100"), scale, RoundingMode.HALF_UP);

                if (!"vnd".equalsIgnoreCase(ccyDebit) &&
                        !"usd".equalsIgnoreCase(ccyDebit) &&
                tiGiaUsd != null && tiGiaNgoaiTe != null
                        && "usd".equalsIgnoreCase(promFee.getCcy())) {
                    // ccy tk nguồn ngoại tệ -> quy đổi phí ra usd
                    feeN = feeN.multiply(tiGiaNgoaiTe.getAppXferBuy()).divide(tiGiaUsd.getSellRate(), scale, RoundingMode.HALF_UP);
                }

                feeN = (feeN.compareTo(promFee.getMaxFee()) >= 0 && promFee.getMaxFee().compareTo(BigDecimal.ZERO) != 0)
                        ? promFee.getMaxFee()
                        : feeN;
                feeN = (feeN.compareTo(promFee.getMinFee()) <= 0 && promFee.getMinFee().compareTo(BigDecimal.ZERO) != 0)
                        ? promFee.getMinFee()
                        : feeN;
            } else {
                feeN = promFee.getFeeValue();
            }
            BigDecimal vatN = isExamptVat ? BigDecimal.ZERO : feeN
                    .multiply(new BigDecimal(promFee.getVat()))
                    .divide(new BigDecimal("100"), scale, RoundingMode.HALF_UP);
            return new FeeTransferDTO(feeN, vatN, BigDecimal.ZERO, ccy);
        } catch (Exception e) {
            return new FeeTransferDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, ccy);
        }
    }

    private FeeTransferDTO makeFeeAndVat(BigDecimal amount, MbPkgServiceFee fee, String ccy, boolean isExamptVat,
                                         String ccyDebit, ExchangeRateInquiryBankResponse tiGiaUsd,
                                         ExchangeRateInquiryBankResponse tiGiaNgoaiTe) {
        log.info("makeFeeAndVat@" + gson.toJson(fee));
//        log.info("amount {} ccy {} isExamptVat {} ccyDebit {} tiGiaUsd {} tiGiaNgoaiTe {}", amount,
//                ccy, isExamptVat, ccyDebit, gson.toJson(tiGiaUsd), gson.toJson(tiGiaNgoaiTe));
        try {
            int scale = "VND".equalsIgnoreCase(ccy) ? 0 : 2;
            BigDecimal feeN;
            if ("Percent".equals(fee.getFeeType())) {
                feeN = fee.getFeeValue()
                        .multiply(amount)
                        .divide(new BigDecimal("100"), scale, RoundingMode.HALF_UP);

                if (!"vnd".equalsIgnoreCase(ccyDebit) &&
                        !"usd".equalsIgnoreCase(ccyDebit) &&
                        tiGiaUsd != null && tiGiaNgoaiTe != null
                && "usd".equalsIgnoreCase(fee.getCcy())) {
                    // ccy tk nguồn ngoại tệ -> quy đổi phí ra usd
                    feeN = feeN.multiply(tiGiaNgoaiTe.getAppXferBuy()).divide(tiGiaUsd.getSellRate(), scale, RoundingMode.HALF_UP);
                }

                feeN = (feeN.compareTo(fee.getMaxFee()) >= 0 && fee.getMaxFee().compareTo(BigDecimal.ZERO) != 0)
                        ? fee.getMaxFee()
                        : feeN;
                feeN = (feeN.compareTo(fee.getMinFee()) <= 0 && fee.getMinFee().compareTo(BigDecimal.ZERO) != 0)
                        ? fee.getMinFee()
                        : feeN;
            } else {
                feeN = fee.getFeeValue();
            }
            BigDecimal vatN = isExamptVat ? BigDecimal.ZERO : feeN
                    .multiply(new BigDecimal(fee.getVat()))
                    .divide(new BigDecimal("100"), scale, RoundingMode.HALF_UP);
            return new FeeTransferDTO(feeN, vatN, BigDecimal.ZERO, ccy);
        } catch (Exception e) {
            return new FeeTransferDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, ccy);
        }
    }
}
