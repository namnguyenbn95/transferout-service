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
import vn.vnpay.request.CashPaymentRequest;
import vn.vnpay.request.InitConfirmTransBatchRequest;
import vn.vnpay.service.CashPaymentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CashPaymentController {

    @Autowired
    private CashPaymentService cashPaymentService;

    @PostMapping(value = "/maker/init-cash-payment")
    public ResponseEntity<BaseClientResponse> makerInitCashPayment(
            @Valid @RequestBody CashPaymentRequest req) {
        BaseClientResponse resp = cashPaymentService.makerInitCashPayment(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/confirm-cash-payment")
    public ResponseEntity<BaseClientResponse> transferOut_MakerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = cashPaymentService.makerConfirmCashPayment(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/checker/init-cash-payment")
    public ResponseEntity<BaseClientResponse> checkerInitCashPayment(
            @Valid @RequestBody BaseCheckerInitRequest req) {
        BaseClientResponse resp = cashPaymentService.checkerInitCashPayment(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/checker/confirm-cash-payment")
    public ResponseEntity<BaseClientResponse> transferConfirm(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = cashPaymentService.checkerConfirmCashPayment(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/batch/init-cash-payment")
    public ResponseEntity<BaseClientResponse> transferBatchInitConfirm(@Valid @RequestBody InitConfirmTransBatchRequest req) {
        BaseClientResponse resp = cashPaymentService.initBatchCashPayment(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/batch/confirm-cash-payment")
    public ResponseEntity<BaseClientResponse> transferBatchConfirm(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = cashPaymentService.confirmBatchCashPayment(req);
        return ResponseEntity.ok(resp);
    }
}
