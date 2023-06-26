package vn.vnpay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpay.commoninterface.request.BaseCheckerInitRequest;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.request.CheckCutOffTimeRequest;
import vn.vnpay.request.TransferOutRequest;
import vn.vnpay.service.TransferOutService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class TransferOutController {
    @Autowired
    private TransferOutService transferOutService;

    @PostMapping(value = "/check-cutoff-time")
    public ResponseEntity<BaseClientResponse> checkCutOffTime(
            @Valid @RequestBody CheckCutOffTimeRequest req) {
        BaseClientResponse resp = transferOutService.checkCutOffTime(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/init")
    public ResponseEntity<BaseClientResponse> transferOut_MakerInit(
            @Valid @RequestBody TransferOutRequest req) {
        BaseClientResponse resp = transferOutService.makerInit(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/confirm")
    public ResponseEntity<BaseClientResponse> transferOut_MakerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transferOutService.makerConfirm(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/checker/init")
    public ResponseEntity<BaseClientResponse> transferOut_CheckerInit(
            @Valid @RequestBody BaseCheckerInitRequest req) {
        BaseClientResponse resp = transferOutService.checkerInit(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/checker/confirm")
    public ResponseEntity<BaseClientResponse> transferOut_CheckerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transferOutService.checkerConfirm(req);
        return ResponseEntity.ok(resp);
    }
}
