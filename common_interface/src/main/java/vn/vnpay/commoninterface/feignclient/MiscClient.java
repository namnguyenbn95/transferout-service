package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.request.pcm.GetAutoDebitUserInfoRequest;
import vn.vnpay.commoninterface.bank.request.pcm.ListAutoDebitUserRequest;
import vn.vnpay.commoninterface.bank.request.pcm.RegisterAutoDebitUserRequest;
import vn.vnpay.commoninterface.bank.request.pcm.UnRegAutoDebitUserRequest;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.bank.response.billing.GetAutoDebitUserInfoResponse;
import vn.vnpay.commoninterface.bank.response.pcm.ListAutoDebitUserResponse;

@FeignClient(name = "digibank-integration-misc-service")
public interface MiscClient {

    /**
     * Lấy danh sách Ecom Merchant
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetEcomMidList")
    GetEcomMidListBankResponse getEcomMidList(BaseBankRequest req);

    /**
     * Lấy danh sách Ecom Terminal
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetEcomTidList")
    GetEcomTidListBankResponse getEcomTidList(GetEcomTidListBankRequest req);


    /**
     * Đăng ký giao dịch ngày tương lai/định kỳ
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/RegisterFutureTrans")
    RegisterFutureTransBankResponse registerFutureTrans(RegisterFutureTransBankRequest req);

    /**
     * Kiểm tra số tài khoản có phải là tài khoản ảo hay không
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/VRAValidate")
    VRAValidateBankResponse validateVRA(VRAValidateBankRequest req);

    /**
     * Ghi log giao dịch tài khoản ảo
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/VRALogTransaction")
    VRALogTransactionBankResponse logVRATrans(VRALogTransactionBankRequest req);


    /**
     * Lấy danh sách lãi suất
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/ListInrate")
    ListInRateBankResponse getMiscListInRate(ListInrateBankRequest req);

    /**
     * Lấy danh sách tỷ giá (phục vụ vẽ biểu đồ)
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/ListExrate")
    ListExRateBankResponse getListExrate(BaseBankRequest req);

    /**
     * Tính toán tỷ giá
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/CalculateExrate")
    CalculateExrateBankResponse calculateExrate(CalculateExrateBankRequest req);

    /**
     * Lấy danh sách Tỉnh/Thành
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetProvinces")
    GetProvincesBankResponse getProvinces(GetProvincesBankRequest req);

    /**
     * Lấy danh sách Quận/Huyện
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetDistricts")
    GetDistrictsBankResponse getDistricts(GetDistrictsBankRequest req);

    /**
     * Lấy danh sách Điểm giao dịch
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetOffices")
    GetOfficesBankResponse getOffices(GetOfficesBankRequest req);

    /**
     * Lấy danh sách điểm ATM
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetATMs")
    GetATMsBankResponse getATMs(GetOfficesBankRequest req);

    /**
     * Lấy danh sách gdich
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/ListFutureTrans")
    ListFutureTransBankResponse getListFutureTrans(ListFutureTransBankRequest req);

    /**
     * Hủy 1 giao dịch ngày tương lai/định kỳ
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/CancelFutureTrans")
    BaseBankResponse cancelFutureTrans(CancelFutureTransBankRequest req);

    /**
     * Lấy thông tin chi tiết 1 giao dịch ngày tương lai/định kỳ
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/DetailFutureTrans")
    DetailFutureTransBankResponse detailFutureTrans(CancelFutureTransBankRequest req);

    /**
     * Hủy toàn bộ giao dịch ngày tương lai/định kỳ
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/CancelAllFutureTrans")
    BaseBankResponse cancelAllFutureTrans(CancelAllFutureTransBankReq req);

    /**
     * Cập nhật thông tin tài khoản số đẹp, số thẻ đăng ký của KH
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/RegisterAccountCardFeeCombo")
    BaseBankResponse registAccCardFeeCombo(RegistAccCardFeeComboBankReq req);

    /**
     * Truy vấn thông tin đăng ký của Khách hàng
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetFeeCombo")
    GetFeeComboBankResponse getFeeCombo(GetFeeComboBankReq req);

    /**
     * Lấy danh mục gói phí được cài đặt
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetListFeeCodeCombo")
    GetListFeeCodeComBoBankResponse getListFeeCodeCombo(GetListFeeCodeComboBankReq req);

    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetAutoDebitUserInfo")
    GetAutoDebitUserInfoResponse getBLWPayerInfo(GetAutoDebitUserInfoRequest req);

    /**
     * Đăng ký trích nợ tự động hóa đơn
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/RegisterAutoDebitUser")
    BaseBankResponse regBLWPayer(RegisterAutoDebitUserRequest req);

    /**
     * Lấy danh sách bản ghi đăng ký trích nợ tự động
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetListAutoDebitUser")
    ListAutoDebitUserResponse listAutoDebit(ListAutoDebitUserRequest req);

    /**
     * Hủy bản ghi đăng ký trích nợ tự động
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/UnRegisterAutoDebitUser")
    BaseBankResponse unRegAutoDebit(UnRegAutoDebitUserRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Misc/QueryCustomerInfo")
    QueryCustomerInfoBankResponse queryCustomerInfo(GetAutoDebitUserInfoRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Misc/HistoryFutureTrans")
    HistoryFutureTransBankResp historyFutureTrans(HistoryFutureTransBankReq req);

    /**
     * Lưu thông tin đăng ký của Khách hàng
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Misc/RegisterFeeCombo")
    BaseBankResponse registerFeeCombo(RegisterFeeComboBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/Misc/GetInfoNPS")
    GetInfoNPSBankResp getInfoNPSBank(GetInfoNPSBankReq req);

    @RequestMapping(method = RequestMethod.POST, value = "/Misc/CancelNPS")
    BaseBankResponse cancelNPSBank(CancelNPSBankReq req);
}

