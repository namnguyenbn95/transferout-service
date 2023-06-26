package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.CheckBene247ViaAccountBankRequest;
import vn.vnpay.commoninterface.bank.request.CheckBene247ViaCardBankRequest;
import vn.vnpay.commoninterface.bank.request.TransferOutBankRequest;
import vn.vnpay.commoninterface.bank.response.CheckBene247BankResponse;
import vn.vnpay.commoninterface.bank.response.TransferOutBankResponse;

@FeignClient(name = "digibank-integration-quicktrans-service")
public interface Tranfer247Client {

    // Truy vấn thông tin tài khoản qua số tài khoản
    @RequestMapping(method = RequestMethod.POST, value = "/QuickTrans/Query247ViaAccount")
    CheckBene247BankResponse checkBene247ViaAccount(CheckBene247ViaAccountBankRequest req);

    // Truy vấn thông tin tài khoản qua số thẻ
    @RequestMapping(method = RequestMethod.POST, value = "/QuickTrans/Query247ViaCard")
    CheckBene247BankResponse checkBene247ViaCard(CheckBene247ViaCardBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/QuickTrans/Transfer247ViaAccount")
    TransferOutBankResponse transfer247ViaAccount(TransferOutBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/QuickTrans/Transfer247ViaCard")
    TransferOutBankResponse transfer247ViaCard(TransferOutBankRequest req);
}
