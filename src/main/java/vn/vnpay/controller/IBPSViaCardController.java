package vn.vnpay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.request.BaseConfirmRq;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.commoninterface.service.RedisCacheService;
import vn.vnpay.dbinterface.entity.SmeCustomerUser;
import vn.vnpay.request.IBPSViaCardRequest;
import vn.vnpay.service.IBPSViaCardService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class IBPSViaCardController {

    @Autowired
    CommonService commonService;

    @Autowired
    RedisCacheService cacheService;

    @Autowired
    IBPSViaCardService ibpsViaCardService;

    /**
     * Maker Khởi tạo giao dịch chuyển tiền IBPS qua thẻ
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/ibps/via-card/maker/init")
    public ResponseEntity<BaseClientResponse> ibpsViaCardMakerInit(@Valid @RequestBody IBPSViaCardRequest req) {
        BaseClientResponse baseResp;
        try {
            SmeCustomerUser user = cacheService.getCustomerUser(req);
            if (user.getRoleType().equals(Constants.UserRole.CHECKER)) {
                baseResp = commonService.makeClientResponse(Constants.ResCode.USER_102, commonService.getMessage(Constants.MessageCode.USER_102, req.getLang()));
                return ResponseEntity.ok(baseResp);
            }
            switch (user.getCusUserStatus()) {
                case Constants.UserStatus.ACTIVE:
                    baseResp = ibpsViaCardService.ibpsViaCardMakerInit(req, user);
                    break;
                default:
                    log.info("Invalid user status");
                    baseResp = commonService.makeClientResponse(Constants.ResCode.USER_100, commonService.getMessage(Constants.MessageCode.USER_100, req.getLang()));
                    break;
            }
        } catch (Exception e) {
            log.info("Error: ", e);
            baseResp = commonService.makeClientResponse(Constants.ResCode.ERROR_96, commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        return ResponseEntity.ok(baseResp);
    }

    /**
     * Maker Xác nhận giao dịch chuyển tiền IBPS qua thẻ
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/ibps/via-card/maker/confirm")
    public ResponseEntity<BaseClientResponse> ibpsViaCardMakerInit(@Valid @RequestBody BaseConfirmRq req) {
        BaseClientResponse baseResp;
        try {
            SmeCustomerUser user = cacheService.getCustomerUser(req);
            if (user.getRoleType().equals(Constants.UserRole.CHECKER)) {
                baseResp = commonService.makeClientResponse(Constants.ResCode.USER_102, commonService.getMessage(Constants.MessageCode.USER_102, req.getLang()));
                return ResponseEntity.ok(baseResp);
            }
            switch (user.getCusUserStatus()) {
                case Constants.UserStatus.ACTIVE:
                    baseResp = ibpsViaCardService.ibpsViaCardMakerConfirm(req, user);
                    break;
                default:
                    log.info("Invalid user status");
                    baseResp = commonService.makeClientResponse(Constants.ResCode.USER_100, commonService.getMessage(Constants.MessageCode.USER_100, req.getLang()));
                    break;
            }
        } catch (Exception e) {
            log.info("Error: ", e);
            baseResp = commonService.makeClientResponse(Constants.ResCode.ERROR_96, commonService.getMessage(Constants.MessageCode.ERROR_96, req.getLang()));
        }
        return ResponseEntity.ok(baseResp);
    }
}
