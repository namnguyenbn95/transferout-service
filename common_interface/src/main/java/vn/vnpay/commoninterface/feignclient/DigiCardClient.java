package vn.vnpay.commoninterface.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.vnpay.commoninterface.bank.request.*;
import vn.vnpay.commoninterface.bank.response.*;

@FeignClient(name = "digibank-integration-card-service")
public interface DigiCardClient {

    /**
     * Lấy danh sách thẻ tín dụng
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/credit-card/list")
    CardListBankResponse getCreditCardListByCif(CardListByCifBankRequest req);

    /**
     * Lấy danh sách các kỳ sao kê
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/date-statement/list")
    DateStmtListBankResponse getDateStatementList(DateStmtListBankRequest req);

    /**
     * Lấy thông tin kỳ sao kê
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/card-detail/info")
    CardStmtDetailBankResponse getCardStatementDetails(CardStmtDetailBankRequest req);

    /**
     * Lấy danh sách giao dịch đã được sao kê
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/card-statement-hist/list")
    CardStmtHistListBankResponse getCardStatementHistList(CardStmtHistListBankRequest req);

    /**
     * Lấy danh sách giao dịch Chờ sao kê
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/card-transaction-hist/list")
    CardTransHistListBankResponse getCardTransHistList(CardTransHistListBankRequest req);

    /**
     * Lấy danh sách giao dịch Chờ xử lý
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/card-waiting/list")
    CardWaitingBankResponse getCardWaitingList(CardWaitingBankRequest req);

    /**
     * Lấy danh sách thẻ debit
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/debit-card/list")
    DebitCardListBankResponse getDebitCardListByCif(CardListByCifBankRequest req);

    /**
     * Thanh toán thẻ tín dụng
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/statement/update")
    CardStatementUpdateBankResponse creditCardPayment(CardStatementUpdateBankRequest req);

    /**
     * Update Xpac Code
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/sme-card/status/update")
    CardInfoUpdateBankResponse updateXpacCode(CreditCardInfoUpdateBankRequest req);

    /**
     * Lấy danh sách thẻ debit & credit
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/sme-card/list")
    CardListBankResponse getSmeCardListByCif(CardListByCifBankRequest req);

    /**
     * Truy vấn thông tin thẻ tín dụng
     *
     * @param req
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/card/card-statement")
    CardStatementInquiryBankResponse getCreditCardStmt(CardStatementBankRequest req);
}
