package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.request.BaseSoftRequest;
import vn.vnpay.commoninterface.request.SoftTransConfirmRequest;
import vn.vnpay.commoninterface.request.SoftTransInitRequest;
import vn.vnpay.commoninterface.request.softotp.BlockSoftRq;
import vn.vnpay.commoninterface.request.softotp.CancelSoftListReq;
import vn.vnpay.commoninterface.request.softotp.ConfirmSoftOtpRq;
import vn.vnpay.commoninterface.request.softotp.InitSoftOtpRq;
import vn.vnpay.commoninterface.response.SoftTransInitResponse;
import vn.vnpay.commoninterface.response.softotp.*;

@FeignClient(name = "soft-otp")
public interface SmeSoftOtpClient {
    @RequestMapping(method = RequestMethod.POST, value = "/soft/transaction/init")
    SoftTransInitResponse transactionInit(@RequestBody SoftTransInitRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/transaction/confirm")
    SoftTransInitResponse transactionConfirm(@RequestBody SoftTransConfirmRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/block")
    BlockSoftRp block(@RequestBody BlockSoftRq req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/active/confirm")
    ConfirmSoftOtpRp activeConfirm(@RequestBody ConfirmSoftOtpRq req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/active/init")
    InitSoftOtpRp activeInit(@RequestBody InitSoftOtpRq req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/user/update/status")
    CheckStatusRp updateStatus(@RequestBody BaseSoftRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/user/check/status")
    CheckStatusRp checkStatus(@RequestBody BaseSoftRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/soft/cancel")
    CancelSoftResponse cancel(@RequestBody CancelSoftListReq req);
}