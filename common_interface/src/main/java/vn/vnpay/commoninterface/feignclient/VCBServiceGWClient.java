package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.request.pcm.*;
import vn.vnpay.commoninterface.bank.response.*;
import vn.vnpay.commoninterface.bank.response.pcm.*;

@FeignClient(name = "digibank-integration-billpay-service")
public interface VCBServiceGWClient {
    /**
     * Get list biller via service code
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMBillerDetails")
    PcmListBillerResponse getAllBiller(PcmListBillerRequest req);

    /**
     * Get list division of biller
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMBillerDivisionInq")
    PcmListDivisionResponse getAllBillerDivision(PcmListDivisionRequest req);

    /**
     * Get payment config
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMBillPaymentConfigInq")
    PcmPaymentConfigResponse paymentConfig(PcmPaymentConfigRequest req);

    /**
     * Get payment config
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMPayerRegConfigInq")
    PcmPaymentConfigResponse autodebitConfig(PcmPaymentConfigRequest req);

    /**
     * Get pcm bill info
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMBillInfoInq")
    PcmBillInfoInqResponse billInfoInq(PcmBillInfoInqRequest req);

    /**
     * Get esb bill info
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BLGService/api/BLGQueryBill")
    BLGWQueryResponse blgwQuery(BLGWQueryRequest req);

    /**
     * Pay esb bill
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BLGService/api/BLGPayBill")
    BLGWPayResponse blgwPay(BLGWPayRequest req);

    /**
     * Lấy danh sách Cơ quan thuế
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListCqt")
    GetListCqtBankResponse getListCqt(@RequestBody BaseBankRequest req);

    /**
     * Lấy danh sách Kho bạc tổng hợp
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListKbth")
    GetListKbthBankResponse getListKbth(@RequestBody BaseBankRequest req);

    /**
     * Lấy danh sách Kho bạc chi tiết
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListKbct")
    GetListKbctBankResponse getListKbct(@RequestBody GetListKbctBankRequest req);

    /**
     * Lấy thông tin chi tiết mã số thuế
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetSoThue")
    GetSoThueBankResponse getSoThue(@RequestBody GetSoThueBankRequest req);

    /**
     * Lấy danh sách Nội dung kinh tế
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListNdkt")
    GetListNdktBankResponse getListNdkt(@RequestBody BaseBankRequest req);

    /**
     * Lấy danh sách Chương
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListChuong")
    GetListChuongBankResponse getListChuong(@RequestBody BaseBankRequest req);

    /**
     * Lấy thông tin địa bàn hành chính
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListDbhcCity")
    GetListDbhcCityBankResponse getListDbhcCity(@RequestBody BaseBankRequest req);

    /**
     * Lấy thông tin địa bàn hành chính của tỉnh
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListDbhc")
    GetListDbhcBankResponse getListDbhc(@RequestBody GetListDbhcBankRequest req);

    /**
     * Pay pcm bill
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMBillPaymentAdd")
    PcmBillInfoInqResponse pcmPayBill(PcmPaymentAddRequest req);

    /**
     * Pay pcm bill
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMPayerChargeCalculation")
    PcmPaymentFeeResponse pcmCalFee(PcmPaymentFeeRequest req);

    /**
     * Lấy thông tin ghi có kho bạc nhà nước
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetRMInfo")
    GetRMInfoBankResponse getRMInfo(@RequestBody GetRMInfoBankRequest req);

    /**
     * Tra cứu Lệ phí trước bạ
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/TraCuuLptb")
    TraCuuLptbBankResponse traCuuLptb(@RequestBody TraCuuLptbBankRequest req);

    /**
     * Gửi báo có và ghi log
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/SendPaymentDataAndTransLog")
    SendPaymentDataAndTransLogBankResponse sendPaymentDataAndTransLog(@RequestBody SendPaymentDataAndTransLogBankRequest req);

    /**
     * Gửi báo có và ghi log lệ phí trước bạ
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/SendPaymentDataAndTransLogLPTB")
    SendPaymentDataAndTransLogLPTBBankResponse sendPaymentDataAndTransLogLPTB(@RequestBody SendPaymentDataAndTransLogLPTBBankRequest req);

    /**
     * Lấy danh sách thành phố
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BHXHService/BHXHGetCity")
    BHXHGetCityBankResponse bhxhGetCity(@RequestBody BaseBankRequest req);

    /**
     * Lấy danh sách cơ quan bảo hiểm
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BHXHService/BHXHGetCompany")
    BHXHGetCompanyBankResponse bhxhGetCompany(@RequestBody BHXHGetCompanyBankRequest req);

    /**
     * Truy vấn dữ liệu BHXH
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BHXHService/BHXHGetData")
    BHXHGetDataBankResponse bhxhGetData(@RequestBody BHXHGetDataBankRequest req);

    /**
     * Hạch toán BHXH
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/BHXHService/BHXHPayment")
    BHXHPaymentBankResponse bhxhPayment(@RequestBody BHXHPaymentBankRequest req);

    /**
     * Cảng biển: Lấy danh mục phí
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortGetDMPhi")
    SeaPortGetDMPhiBankResponse getDMPhi(@RequestBody BaseBankRequest req);

    /**
     * Cảng biển: Lấy danh mục đơn vị thu phí
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortGetDMDVThuPhi")
    SeaPortGetDMDVThuPhiBankResponse getDMDVThuPhi(@RequestBody BaseBankRequest req);

    /**
     * Truy vấn dữ liệu phí cảng biển
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortGetPhiCangData")
    SeaPortGetPhiCangDataBankResponse getPhiCangData(@RequestBody SeaPortGetPhiCangDataBankRequest req);

    /**
     * Hạch toán thu phí cảng biển
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortPayment")
    SeaPortPaymentBankResponse seaportPayment(@RequestBody SeaPortPaymentBankRequest req);

    /**
     * Truy vấn dữ liệu phí cảng biển HCM
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortGetPhiCangDataHCM")
    SeaPortGetPhiCangDataHCMBankResponse getPhiCangDataHCM(@RequestBody SeaPortGetPhiCangDataHCMBankRequest req);

    /**
     * Hạch toán thu phí cảng biển HCM
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/NSNNSeaPort/NSNNSeaPortPaymentHCM")
    SeaPortPaymentHCMBankResponse seaportPaymentHCM(@RequestBody SeaPortPaymentHCMBankRequest req);

    /**
     * Lấy danh sách Hải quan
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListHq")
    GetListHqBankResponse getListHq(@RequestBody BaseBankRequest req);

    /**
     * Lấy danh sách Loại hình XNK
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/GetListTypeXNK")
    GetListTypeXnkBankResponse getListTypeXNK(@RequestBody BaseBankRequest req);

    /**
     * Tra cứu thuế Hải quan
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/TraCuuThueHq")
    TraCuuThueHqBankResponse traCuuThueHq(@RequestBody TraCuuThueHqBankRequest req);

    /**
     * Hạch toán và báo có thuế hải quan
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/XacNhanNopThueHQ")
    XacNhanNopThueHqBankResponse xacNhanNopThueHQ(@RequestBody XacNhanNopThueHqBankRequest req);

    /**
     * Get pcm customer info
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMPayerInq")
    PcmPayerInfoInqResponse payerInfoInq(PCMPayerInqRequest req);

    /**
     * Get pcm bill info
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMCustomerBilling")
    PcmBillInfoInqResponse customerBilling(PCMCustomerBillingRequest req);

    /**
     * Get pcm auto debit list
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PCMService/api/PCMCustomerBillingInq")
    PcmCustomerBillingInqResponse customerBillingInq(PCMCustomerBillingRequest req);


    /**
     * Lấy danh sách đối tác ví điện tử
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletGetListPartner")
    EwalletGetListPartnerBankResponse eWalletGetListPartner(EwalletGetLstPartnerBankRequest req);

    /**
     * Lấy danh sách ví đã map theo CIF và mã nhà cung cấp
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletGetListCustByCIFPartner")
    EwalletGetListCustByCIFPartnerResponse eWalletGetListCustByCIFPartner(GetListCustByCIFPartnerBankRequest req);

    /**
     * Kiểm tra thông tin khách hàng bên đối tác để liên kết
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletCheckActive")
    EwalletCheckActiveBankResponse eWalletCheckActive(EwalletCheckActiveRequest req);

    /**
     * Liên kết ví điện tử
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletActive")
    BaseBankResponse eWalletActive(EwalletActiveBankRequest req);

    /**
     * Hủy liên kết ví điện tử
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletDeactive")
    BaseBankResponse eWalletDeactive(EwalletDeActiveBankRequest req);

    /**
     * Cập nhật thông tin ví điện tử tại đầu VCB
     */
    @RequestMapping(method = RequestMethod.POST, value = "/EWalletService/EwalletUpdateCustVCBInfo")
    BaseBankResponse eWalletUpdateCustVCBInfo(EwalletUpdateCusBankRequest req);

    /**
     * Tra cứu phí Hải quan
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/TraCuuPhiHq")
    TraCuuPhiHqBankResponse traCuuPhiHq(@RequestBody TraCuuPhiHqBankRequest req);


    /**
     * Hạch toán và báo có phí hải quan
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/TaxService/XacNhanNopPhiHq")
    XacNhanNopPhiHqBankResponse xacNhanNopPhiHq(@RequestBody XacNhanNopPhiHqBankRequest req);

    /**
     * ham lay chi tiet giao dich de phuc vu tao lenh tra soat
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/GetTransactionInfo")
    GetTransChargebackInfoBankResponse getDetailTransChargeback(@RequestBody GetTransChargebackInfoBankRequest req);

    /**
    * ham lay trạng thái giao dịch
    * @param req
    * @return
    */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/GetIBPSTransactionStatus")
    GetIBPSTransStatusBankResponse getIBPSTransStatus(@RequestBody GetIBPSTransStatusBankRequest req);

    /**
     * tạo giao dịch tra soát
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/CreateTransaction")
    CreateTransChargebackBankResponse createTransChargeback(@RequestBody CreateTransChargebackBankRequest req);

    /**
     * lấy danh sách giao dịch đã tra soát
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/GetListTransaction")
    GetListTransChargebackBankResponse getListTransChargeback(@RequestBody GetListTransChargebackBankRequest req);

    /**
     * lấy chi tiết giao dịch đã tra soát
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/GetTransactionByTSOLRef")
    GetDetailCreateTransChargebackBankResponse getCreateTransChargeback(@RequestBody GetDetailCreateTransChargebackBankReq req);

    /**
     * lấy danh sách teller tra soát
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/Tsol/GetListOnlineTeller")
    GetListTellerChargebackBankResp getListTellerChargeback(@RequestBody BaseBankRequest req);
}
