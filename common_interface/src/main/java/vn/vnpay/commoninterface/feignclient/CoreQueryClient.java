package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;

@FeignClient(name = "digibank-integration-corequery-service")
public interface CoreQueryClient {

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetCustomerInforByCif")
    GetCustomerInforByCifBankResponse getCustomerInforByCif(GetCustomerInforByCifBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/AccountListing")
    AccountListingBankResponse getAccountListByCif(AccountListingBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/DDAccountDetail")
    AccountDetailBankResponse getDDAccountDetails(AccountDetailBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/FDAccountDetail")
    AccountDetailBankResponse getFDAccountDetails(AccountDetailBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/LNAccountDetail")
    AccountDetailBankResponse getLNAccountDetails(AccountDetailBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/DDAccountHistory")
    AccountHistoryBankResponse getDDAccountHistory(AccountHistoryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/FDAccountHistory")
    AccountHistoryBankResponse getFDAccountHistory(AccountHistoryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/LNAccountHistory")
    AccountHistoryBankResponse getLNAccountHistory(AccountHistoryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/ExchangeRateInquiry")
    ExchangeRateInquiryBankResponse getExchangeRateInquiry(ExchangeRateInquiryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetTxnTransactionDetail")
    TxnDetailBankResponse getTxnDetail(TxnDetailBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/AccountStatusInquiry")
    AccountStatusInquiryBankResponse accountStatusInquiry(AccountStatusInquiryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetHostDate")
    GetBankHostDateResponse getHostDate(BaseBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetCumulativeBalByDate")
    GetCumulativeBalByDateBankResponse getCumulativeBalByDate(GetCumulativeBalByDateBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/DDAccountHistoryPrint")
    AccountHistoryBankResponse getDDAccountHistoryPrint(AccountHistoryBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetCountAccountOwner")
    GetCountAccountOwnerBankResponse getCountAccOwner(GetCountAccountOwnerBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/DDAcctNumSelectListInquiry")
    GetDDAcctNumSelectListInqResponse getDDAcctNumSelectListInquiry(GetDDAcctNumSelectListInquiry req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/DDAcctNumSelectPriceInquiry")
    GetDDAcctNumSelectPriceInqResponse getDDAcctNumSelectPriceInquiry(GetDDAcctNumSelectPriceInquiry req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetAvgBalanceByMonth")
    GetAvgBalanceByMonthBankResponse getAvgBalanceByMonth(GetAvgBalanceByMonthBankReq req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetCashFlowList")
    GetCashFlowListBankResponse getCashFlowList(GetCashFlowListBankReq req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetFeeLNRepayment")
    GetFeeLNRepaymentResponse getFeeLNRepayment(GetFeeLNRepaymentRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetLNAccountList")
    GetLNAccountListResponse getLNAccountList(GetLNAccountListRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/CoreQuery/GetLoanAccountDetail")
    GetLoanAccountDetailResponse getLoanAccountDetail(GetLoanAccountDetailRequest req);
}
