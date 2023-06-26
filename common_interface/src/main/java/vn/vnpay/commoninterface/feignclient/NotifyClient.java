package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;

@FeignClient(name = "digibank-integration-notify-service")
public interface NotifyClient {

    @RequestMapping(method = RequestMethod.POST, value = "/OTTService/StopOTT")
    StopOTTBankResponse stopOTTService(StopOTTBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/SendSMS")
    SendSmsBankResponse sendSms(SendSmsBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/OTTService/CheckOTT")
    CheckOTTBankResponse checkOTTService(CheckOTTBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/OTTService/RegisterOTT")
    RegisterOTTBankResponse registerOTTService(RegisterOTTBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/GetSMSUserByCif")
    GetSMSUserByCifBankResponse getSMSUserByCif(GetSMSUserByCifBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/GetSMSActiveByCif")
    GetSMSUserByCifBankResponse getSMSActiveByCif(GetSMSUserByCifBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/StopCorporateSMS")
    BaseBankResponse stopCorporateSMS(RegisterActiveSMSBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/StopActiveSMS")
    BaseBankResponse stopActiveSMS(RegisterActiveSMSBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/RegisterActiveSMS")
    BaseBankResponse registerActiveSMS(RegisterActiveSMSBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/EmailService/SendEmai")
    ResponseStatus sendEmail(SendMailBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/CheckReminder")
    CheckReminderBankResponse checkReminder(CheckReminderBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/RegisterReminderBatch")
    CheckReminderBankResponse registerReminderBatch(RegisterReminderBatchBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/StopReminder")
    CheckReminderBankResponse stopReminder(StopReminderBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/SMSService/StopReminderBatch")
    CheckReminderBankResponse stopReminderBatch(StopReminderBatchBankRequest req);
}
