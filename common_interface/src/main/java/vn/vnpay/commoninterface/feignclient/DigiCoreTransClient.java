package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;

@FeignClient(name = "digibank-integration-coretrans-service")
public interface DigiCoreTransClient {
    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/TransferInVCB")
    TransferInBankResponse transferIn(TransferInBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/TransferOtherBankIBPS")
    TransferOutBankResponse transferOut(TransferOutBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/TransferGLFee")
    TransferGLFeeBankResponse transferGLFee(TransferGLFeeBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/CreateCustomerAcct")
    CreateCustomerAcctBankResponse createCustomerAcct(CreateCustomerAcctBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/DDAcctNumSelectUpdate")
    DDAcctNumSelectUpdateBankResponse ddAcctNumSelectUpdate(DDAcctNumSelectUpdateBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/TransferToID")
    CashTransferBankResponse cashTransfer(CashTransferBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreTrans/RepaymentLNAccount")
    RepaymentLNAccountResponse repaymentLNAccount(RepaymentLNAccountRequest req);
}
