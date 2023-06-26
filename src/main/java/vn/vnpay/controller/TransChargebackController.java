package vn.vnpay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.request.BaseCheckerInitRequest;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.request.*;
import vn.vnpay.response.AccountHistoryResponse;
import vn.vnpay.service.TransChargebackService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class TransChargebackController {
    @Autowired
    private TransChargebackService transChargebackService;

    @Autowired
    private CommonService commonService;

    @PostMapping(value = "/chargeback/get-detail")
    public ResponseEntity<BaseClientResponse> getDetailChargeback(
            @Valid @RequestBody GetDetailTransChargebackRequest req) {
        BaseClientResponse resp = transChargebackService.getDetailChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/maker-init")
    public ResponseEntity<BaseClientResponse> makerInitTransChargeback(
            @Valid @RequestBody TransChargebackRequest req) {
        BaseClientResponse resp = transChargebackService.makerInitTransChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/maker-confirm")
    public ResponseEntity<BaseClientResponse> makerConfirmTransChargeback(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transChargebackService.makerConfirmTransChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/checker-init")
    public ResponseEntity<BaseClientResponse> checkerInitTransChargeback(@Valid @RequestBody BaseCheckerInitRequest req) {
        BaseClientResponse resp = transChargebackService.checkerInitTransChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/checker-confirm")
    public ResponseEntity<BaseClientResponse> checkerConfirmTransChargeback(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transChargebackService.checkerConfirmTransChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/get-list")
    public ResponseEntity<BaseClientResponse> getListTransChargeback(
            @Valid @RequestBody GetListTransChargebackReq req) {
        BaseClientResponse resp = transChargebackService.getListTransChargeback(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/get-detail/trans-chargeback")
    public ResponseEntity<BaseClientResponse> getDetailCreateTransCharegeback(
            @Valid @RequestBody GetDetailCreateTransChargebackReq rq) {
        BaseClientResponse resp = transChargebackService.getDetailCreateTransCharegeback(rq);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/list/servicecode")
    public ResponseEntity<BaseClientResponse> getListServiceCodeChargeback(
            @Valid @RequestBody BaseClientRequest rq) {
        BaseClientResponse resp = transChargebackService.getListServiceCodeChargeback(rq);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/list/maker-checker")
    public ResponseEntity<BaseClientResponse> getListMakerChecker(
            @Valid @RequestBody BaseClientRequest rq) {
        BaseClientResponse resp = transChargebackService.getListMakerChecker(rq);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/chargeback/account/history")
    public ResponseEntity<BaseClientResponse> queryAccountHistory(
            @Valid @RequestBody AccountHistoryRequest req) {
        BaseClientResponse resp =
                new BaseClientResponse(
                        Constants.ResCode.INFO_00,
                        commonService.getMessage(Constants.MessageCode.INFO_00, req.getLang()));
        try {
            AccountHistoryResponse data = transChargebackService.queryAccountHistory(resp, req);
            resp.setData(data);
        } catch (Exception e) {
            resp.setCode(Constants.ResCode.ERROR_96);
            resp.setMessage(commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
            log.info("Error: ", e);
        }
        return ResponseEntity.ok(resp);
    }
}
