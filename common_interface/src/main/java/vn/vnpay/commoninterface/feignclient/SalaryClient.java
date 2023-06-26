package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;


@FeignClient(name = "digibank-integration-salary-service")
public interface SalaryClient {

    @RequestMapping(method = RequestMethod.POST, value = "/Salary/DDMastInfoList")
    BatchCheckTranferInBankResponse checkTranferIn(BatchCheckTranferInBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Salary/BenBankInfoList")
    BatchCheckTranferOutBankResponse checkTranferOut(BatchCheckTranferOutBankRequest req);

    //Đẩy lệnh SL TRAN 1 vào Salary
    @RequestMapping(method = RequestMethod.POST, value = "/Salary/InsertSlTran1")
    BaseBankResponse insertSlTran1(InsertSLtran1BankRequest req);

    //Đẩy lệnh SL TRAN 2 vào Salary
    @RequestMapping(method = RequestMethod.POST, value = "/Salary/InsertSlTran2")
    BaseBankResponse insertSlTran2(InsertSLtran2BankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Salary/SelectGrBatch")
    SelectGrBatchBankResponse selectGrBatch(SelectGrBatchBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Salary/SelectSlTran1")
    SelectSlTran1BankResponse selectSlTran1(SelectSlTran1Request req);

    @RequestMapping(method = RequestMethod.POST, value = "/Salary/SelectSlTran2")
    SelectSlTran2BankResponse selectSlTran2(SelectSlTran2Request req);

    //    Đẩy lệnh SL TRAN 1 và vào SalarySL TRAN 2
    @RequestMapping(method = RequestMethod.POST, value = "/Salary/InsertSlTran")
    BaseBankResponse insertSlTran(InsertSLtranBankRequest req);

}
