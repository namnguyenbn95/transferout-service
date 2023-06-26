package vn.vnpay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.request.CheckBene247Request;
import vn.vnpay.request.Transfer247ViaAccountRequest;
import vn.vnpay.service.Transfer247ViaAccountService;
import vn.vnpay.service.Transfer247ViaCardService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class Tranfer247Controller {

    @Autowired
    Transfer247ViaAccountService transfer247Service;
    @Autowired
    Transfer247ViaCardService transfer247ViaCardService;

    @PostMapping(value = "/check-account")
    public ResponseEntity<BaseClientResponse> checkCutOffTime(
            @Valid @RequestBody CheckBene247Request req) {
        BaseClientResponse resp = transfer247Service.checkBene247(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/init-247-acc")
    public ResponseEntity<BaseClientResponse> transferOut_MakerInit(
            @Valid @RequestBody Transfer247ViaAccountRequest req) {
        BaseClientResponse resp = transfer247Service.makerInitViaAccount(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/confirm-247-acc")
    public ResponseEntity<BaseClientResponse> transferOut_MakerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transfer247Service.makerConfirmViaAccount(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/init-247-card")
    public ResponseEntity<BaseClientResponse> transfer247ViaCardMakerInit(
            @Valid @RequestBody Transfer247ViaAccountRequest req) {
        BaseClientResponse resp = transfer247ViaCardService.makerInitViaCard(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/maker/confirm-247-card")
    public ResponseEntity<BaseClientResponse> ttransfer247ViaCardMakerConfirm(
            @Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse resp = transfer247ViaCardService.makerConfirmViaCard(req);
        return ResponseEntity.ok(resp);
    }
}
