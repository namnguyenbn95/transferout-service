package vn.vnpay.controller;

import com.google.gson.Gson;
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
import vn.vnpay.request.InitConfirmTransBatchRequest;
import vn.vnpay.service.TransferOutService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class TransManagementController {
    @Autowired
    private TransferOutService transferOutService;
    private final Gson gson = new Gson();

    @PostMapping(value = "/trans-management/init")
    public ResponseEntity<BaseClientResponse> transferOut_CheckerInit(
            @Valid @RequestBody BaseCheckerInitRequest req) {
        log.info("Req: " + gson.toJson(req));
        BaseClientResponse resp = transferOutService.transConfirmInit(req);
        log.info("Res: " + gson.toJson(resp));
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/trans-management/confirm")
    public ResponseEntity<BaseClientResponse> transferOut_CheckerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        log.info("Req: " + gson.toJson(req));
        BaseClientResponse resp = transferOutService.transConfirm(req);
        log.info("Res: " + gson.toJson(resp));
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/trans-management/batch/init")
    public ResponseEntity<BaseClientResponse> transferBatchInitConfirm(@Valid @RequestBody InitConfirmTransBatchRequest req) {
        BaseClientResponse resp = transferOutService.initTransBatchConfirm(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/trans-management/batch/confirm")
    public ResponseEntity<BaseClientResponse> transferBatchConfirm(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transferOutService.transBatchConfirm(req);
        return ResponseEntity.ok(resp);
    }
}