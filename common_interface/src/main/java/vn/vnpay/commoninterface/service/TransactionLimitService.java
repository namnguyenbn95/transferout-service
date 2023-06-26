package vn.vnpay.commoninterface.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.vnpay.commoninterface.bank.request.ExchangeRateInquiryBankRequest;
import vn.vnpay.commoninterface.bank.response.ExchangeRateInquiryBankResponse;
import vn.vnpay.commoninterface.common.CommonUtils;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.common.RestClient;
import vn.vnpay.commoninterface.dto.CheckLimitTrans;
import vn.vnpay.commoninterface.feignclient.CoreQueryClient;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

@Slf4j
@Service
public class TransactionLimitService {

    @Autowired
    private Gson gson;

    @Autowired
    RestClient restClient;

    @Autowired
    private Environment env;

    @Autowired
    private CommonService commonService;

    @Autowired
    @Qualifier("dbOnEntityManager")
    EntityManager entityManager;

    @Autowired
    private CoreQueryClient coreQueryClient;

    /**
     * Check hạn mức của giao dịch
     *
     * @param user
     * @return : code:00 pass , desc mô ta lỗi
     */
    public BaseClientResponse checkTranLimit(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, boolean isExecTrans) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {
            log.info("checkTranLimit user={},  cif={}, roleType={} , source={}, package={} ,serviceCode={}, amount={}, ccy={}, authenMethod={}, isExecTrans={}", user.getUsername(), user.getCif(), user.getRoleType(), rq.getSource(), user.getPackageCode(), serviceCode, amount, ccy, authenMethod, isExecTrans);
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.trans")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pkg_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_exec_trans", String.class, ParameterMode.IN)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_role_type", user.getRoleType())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_pkg_code", user.getPackageCode())
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_lang", rq.getLang())
                    .setParameter("p_exec_trans", isExecTrans ? "1" : "0");
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkTranLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Hạn mức thanh toán trực tiếp
     *
     * @param user
     * @param rq
     * @param serviceCode
     * @param providerCode
     * @param type
     * @param accNo
     * @param amount
     * @param ccy
     * @return
     */
    public BaseClientResponse checkPayImmediate(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, String providerCode, String type, String accNo, Double amount, String ccy) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {

            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.pay_immediate")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_acc_no", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pro_code", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", Long.class, ParameterMode.IN)
                    .setParameter("p_type", type)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_acc_no", accNo)
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_pro_code", providerCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_lang", rq.getLang());
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkPayImmediate:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Luu thong tin giao dich de check han muc
     *
     * @param user
     * @param rq
     * @param serviceCode
     * @param amount
     * @param ccy
     * @param authenMethod
     * @param type:        1 Thực hiện ở bước khởi tạo, 2 Thực hiện ở bước confirm
     * @return
     */
    public BaseClientResponse saveCheckTransLimit(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, String type) {
        log.info("saveCheckTransLimit user={}, package ={}, service={}, amount={}, ccy={}, channel={},method={}, type={}", user.getUsername(), user.getPackageCode(), serviceCode, amount, ccy, rq.getSource(), authenMethod, type);
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.ResCode.INFO_00);
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.save_check_tran")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cus_user_id", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_package_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_service_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_method_otp", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_type", String.class, ParameterMode.IN)
                    .setParameter("p_user_name", user.getUsername())
                    .setParameter("p_cus_user_id", user.getCusUserId())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_package_code", user.getPackageCode())
                    .setParameter("p_service_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_method_otp", authenMethod)
                    .setParameter("p_type", type);
            query.execute();
            rp.setMessage("Thanh cong");
        } catch (Exception ex) {
            log.error("Get saveCheckTransLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * @param rp
     * @param ccy:      Tiền tệ gốc
     * @param orgAmount : Số tiền gốc
     * @param vndAmount :Số tiền quy đổi VND
     * @return
     */
    public CheckLimitTrans convertCcyAmount(BaseClientResponse rp, String ccy, BigDecimal orgAmount, BigDecimal vndAmount) {
        String debitCcy = ccy;
        Double debitOrgAmount = orgAmount.doubleValue();
        log.info("sme_check_tran debitCcy: " + debitCcy);
        log.info("sme_check_tran debitOrgAmount: " + debitOrgAmount);
        //Quy đổi hạn mức tính theo VND, USD
        if (!"VND".equals(debitCcy) && !"USD".equals(debitCcy)) {
            Double debitAmountVND = vndAmount.doubleValue();
            debitCcy = "USD";
            BigDecimal rateUSD; // VND
            ExchangeRateInquiryBankRequest bankReq =
                    ExchangeRateInquiryBankRequest.builder().currency("USD").build();
            ExchangeRateInquiryBankResponse bankResp =
                    coreQueryClient.getExchangeRateInquiry(bankReq);
            if (bankResp.getResponseStatus().getIsFail()) {
                log.info("Failed to get exchange rate");
                rp.setCode(bankResp.getResponseStatus().getResCode());
                rp.setMessage(bankResp.getResponseStatus().getResMessage());
            }
            rateUSD = bankResp.getAppXferBuy();
            debitOrgAmount = vndAmount.divide(rateUSD, 2, RoundingMode.HALF_UP).doubleValue();
        }
        log.info("sme_check_tran debitCcy: " + debitCcy);
        log.info("sme_check_tran debitOrgAmount: " + debitOrgAmount);
        return new CheckLimitTrans(debitCcy, debitOrgAmount);
    }


    /**
     * Kiem tra han muc giao dich tuong lai
     *
     * @param user
     * @param rq
     * @param serviceCode
     * @param amount
     * @param ccy
     * @param authenMethod
     * @param isExecTrans
     * @param futurDate:   ngay tuong lai
     * @return
     */
    public BaseClientResponse checkTranLimit(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, boolean isExecTrans, String futureDate) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {
            log.info("checkTranLimit user={},  cif={}, roleType={} , source={}, package={} ,serviceCode={}, amount={}, ccy={}, authenMethod={}, isExecTrans={}, futureDate={}, approveSchedule={}", user.getUsername(), user.getCif(), user.getRoleType(), rq.getSource(), user.getPackageCode(), serviceCode, amount, ccy, authenMethod, isExecTrans, futureDate, "1");
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.trans")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pkg_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_exec_trans", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_future_date", Date.class, ParameterMode.IN)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_role_type", user.getRoleType())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_pkg_code", user.getPackageCode())
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_lang", rq.getLang())
                    .setParameter("p_future_date", CommonUtils.TimeUtils.stringToDate(futureDate, "yyyy-MM-dd"))
                    .setParameter("p_exec_trans", isExecTrans ? "1" : "0");
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkTranLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Lưu thông tin check hạn mức đối với giao dịch  chuyên tien tuong lai
     *
     * @param user
     * @param rq
     * @param serviceCode
     * @param amount
     * @param ccy
     * @param authenMethod
     * @param type
     * @param futureDate:  ngay tuong lai
     * @return
     */
    public BaseClientResponse saveCheckTransLimit(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, String type, String futureDate) {
        log.info("saveCheckTransLimit user={}, package ={}, service={}, amount={}, ccy={}, channel={},method={}, type={}, futureDate={}", user.getUsername(), user.getPackageCode(), serviceCode, amount, ccy, rq.getSource(), authenMethod, type, futureDate);
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.ResCode.INFO_00);
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.save_check_tran")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cus_user_id", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_package_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_service_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_method_otp", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_future_date", Date.class, ParameterMode.IN)
                    .setParameter("p_user_name", user.getUsername())
                    .setParameter("p_cus_user_id", user.getCusUserId())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_package_code", user.getPackageCode())
                    .setParameter("p_service_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_method_otp", authenMethod)
                    .setParameter("p_type", type)
                    .setParameter("p_future_date", CommonUtils.TimeUtils.stringToDate(futureDate, "yyyy-MM-dd"));
            query.execute();
            rp.setMessage("Thanh cong");
        } catch (Exception ex) {
            log.error("Get saveCheckTransLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Check hạn mức của giao dịch lo
     *
     * @param user
     * @return : code:00 pass , desc mô ta lỗi
     */
    public BaseClientResponse checkTranLimitBatch(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, boolean isExecTrans, Double totalCheckLimit) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {
            log.info("checkTranLimit user={},  cif={}, roleType={} , source={}, package={} ,serviceCode={}, amount={}, ccy={}, authenMethod={}, isExecTrans={}, totalCheckLimit={}", user.getUsername(), user.getCif(), user.getRoleType(), rq.getSource(), user.getPackageCode(), serviceCode, amount, ccy, authenMethod, isExecTrans, totalCheckLimit);
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.trans")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pkg_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_exec_trans", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_approve_batch", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_tl_amout_srv_type", Double.class, ParameterMode.IN)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_role_type", user.getRoleType())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_pkg_code", user.getPackageCode())
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_lang", rq.getLang())
                    .setParameter("p_exec_trans", isExecTrans ? "1" : "0")
                    .setParameter("p_approve_batch", "1")
                    .setParameter("p_tl_amout_srv_type", totalCheckLimit);
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkTranLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Kiem tra han muc giao dich tuong lai
     *
     * @param user
     * @param rq
     * @param serviceCode
     * @param amount
     * @param ccy
     * @param authenMethod
     * @param isExecTrans
     * @param futurDate:   ngay tuong lai
     * @return
     */
    public BaseClientResponse checkTranLimitApiFuture(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, boolean isExecTrans, String futureDate) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {
            log.info("checkTranLimit user={},  cif={}, roleType={} , source={}, package={} ,serviceCode={}, amount={}, ccy={}, authenMethod={}, isExecTrans={}, futureDate={}, approveSchedule={}", user.getUsername(), user.getCif(), user.getRoleType(), rq.getSource(), user.getPackageCode(), serviceCode, amount, ccy, authenMethod, isExecTrans, futureDate, "1");
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.trans")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pkg_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_exec_trans", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_future_date", Date.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_approve_schedue", String.class, ParameterMode.IN)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_role_type", user.getRoleType())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_pkg_code", user.getPackageCode())
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_lang", rq.getLang())
                    .setParameter("p_exec_trans", isExecTrans ? "1" : "0")
                    .setParameter("p_future_date", CommonUtils.TimeUtils.stringToDate(futureDate, "yyyy-MM-dd"))
                    .setParameter("p_approve_schedue", "1");
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkTranLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }

    /**
     * Check hạn mức của giao dịch
     *
     * @param user
     * @return : code:00 pass , desc mô ta lỗi
     */
    public BaseClientResponse checkTranLimitApiSchedule(SmeCustomerUser user, BaseClientRequest rq, String serviceCode, Double amount, String ccy, String authenMethod, boolean isExecTrans) {
        BaseClientResponse rp = new BaseClientResponse();
        rp.setCode(Constants.MessageCode.ERROR_96);
        try {
            log.info("checkTranLimit user={},  cif={}, roleType={} , source={}, package={} ,serviceCode={}, amount={}, ccy={}, authenMethod={}, isExecTrans={}", user.getUsername(), user.getCif(), user.getRoleType(), rq.getSource(), user.getPackageCode(), serviceCode, amount, ccy, authenMethod, isExecTrans);
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("sme_trans_limit.trans")
                    .registerStoredProcedureParameter("p_out", String.class, ParameterMode.OUT)
                    .registerStoredProcedureParameter("p_user", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_role_type", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_cif", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_pkg_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_srv_code", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_amount", Double.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_ccy", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_channel", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_authen_method", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_lang", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_exec_trans", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_approve_schedue", String.class, ParameterMode.IN)
                    .setParameter("p_user", user.getUsername())
                    .setParameter("p_role_type", user.getRoleType())
                    .setParameter("p_cif", user.getCif())
                    .setParameter("p_pkg_code", user.getPackageCode())
                    .setParameter("p_srv_code", serviceCode)
                    .setParameter("p_amount", amount)
                    .setParameter("p_ccy", ccy)
                    .setParameter("p_channel", rq.getSource())
                    .setParameter("p_authen_method", authenMethod)
                    .setParameter("p_lang", rq.getLang())
                    .setParameter("p_exec_trans", isExecTrans ? "1" : "0")
                    .setParameter("p_approve_schedue", "1");
            query.execute();
            String result = String.valueOf(query.getOutputParameterValue("p_out"));
            rp.setCode(result.split("\\|")[0]);
            rp.setMessage(result.split("\\|")[1]);
        } catch (Exception ex) {
            log.error("Get checkTranLimit:", ex);
            rp.setCode(Constants.ResCode.ERROR_96);
            rp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, rq.getLang()));
        }
        return rp;
    }
}
