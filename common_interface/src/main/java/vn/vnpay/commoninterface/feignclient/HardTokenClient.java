package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;

@FeignClient(name = "digibank-integration-2fa-service")
public interface HardTokenClient {

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenInquiry")
    HardTokenInquiryResponse inquiryHardToken(HardTokenInquiryRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenActivate")
    HardTokenActiveResponse activeHardToken(HardTokenActiveRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenLock")
    HardTokenLockResponse lockHardToken(HardTokenLockRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/GetTokenChallenge")
    GetTokenChallengeResponse getTokenChallenge(GetTokenChallengeRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenAuthen")
    HardTokenAuthenResponse authenHardToken(HardTokenAuthenRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/GetTokensForAuthen")
    HardTokenForAuthenResponse getTokenForAuthen(HardTokenForAuthenRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenUnregister")
    HardTokenCancelResponse cancelHardToken(HardTokenCancelRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenRegister")
    TokenRegisterBankResponse registerHardToken(TokenRegisterBankRequest req);

    @RequestMapping(method = RequestMethod.POST, value = "/TwoFA/TokenSync")
    BaseBankResponse syncHardToken(TokenSyncBankRequest req);
}
